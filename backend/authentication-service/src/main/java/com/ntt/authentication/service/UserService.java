package com.ntt.authentication.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ntt.authentication.constant.PredefinedRole;
import com.ntt.authentication.domain.OutboxEvent;
import com.ntt.authentication.domain.Role;
import com.ntt.authentication.domain.User;
import com.ntt.authentication.dto.request.*;
import com.ntt.authentication.dto.response.UserResponse;
import com.ntt.authentication.dto.response.UserSearchResponse;
import com.ntt.authentication.exception.AppException;
import com.ntt.authentication.exception.ErrorCode;
import com.ntt.authentication.mapper.UserMapper;
import com.ntt.authentication.repository.OutboxEventRepository;
import com.ntt.authentication.repository.RoleRepository;
import com.ntt.authentication.repository.UserRepository;

import event.dto.UserCreatedEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserRepository userRepository;
    ObjectMapper objectMapper;
    OutboxEventRepository outboxEventRepository;

    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        publishUserCreatedEvent(user, request.getFirstName(), request.getLastName());

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public UserResponse create(UserCreationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();

        if (!CollectionUtils.isEmpty(request.getRoles())) {
            roles.addAll(roleRepository.findAllById(request.getRoles()));
        } else {
            roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);
        }

        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        publishUserCreatedEvent(user, request.getFirstName(), request.getLastName());

        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void delete(String id) {
        if (!userRepository.existsById(id)) throw new AppException(ErrorCode.USER_NOT_FOUND);

        userRepository.deleteById(id);
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getDetail(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void resetPassword(String id, PasswordResetRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse updateRoles(String id, RoleUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        HashSet<Role> roles = new HashSet<>();
        if (!CollectionUtils.isEmpty(request.getRoles())) {
            List<Role> foundRoles = roleRepository.findAllById(request.getRoles());
            if (foundRoles.size() != request.getRoles().size()) throw new AppException(ErrorCode.ROLE_NOT_FOUND);

            roles.addAll(foundRoles);
        }

        user.setRoles(roles);
        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        SecurityContext context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }

    public List<UserSearchResponse> searchByEmail(String email) {
        return userRepository.findTop10ByEmailContainingIgnoreCase(email).stream()
                .map(userMapper::toUserSearchResponse)
                .toList();
    }

    private void publishUserCreatedEvent(User user, String firstName, String lastName) {
        UserCreatedEvent event = UserCreatedEvent.builder()
                .userId(user.getId())
                .firstName(firstName)
                .lastName(lastName)
                .build();
        outboxEventRepository.save(buildOutboxEvent("user.created", event));
    }

    private OutboxEvent buildOutboxEvent(String routingKey, Object payload) {
        return OutboxEvent.builder()
                .routingKey(routingKey)
                .payload(objectMapper.writeValueAsString(payload))
                .status(OutboxEvent.OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .retryCount(0)
                .build();
    }
}
