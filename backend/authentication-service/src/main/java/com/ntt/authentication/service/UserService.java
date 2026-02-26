package com.ntt.authentication.service;

import com.ntt.authentication.constant.PredefinedRole;
import com.ntt.authentication.domain.Role;
import com.ntt.authentication.domain.User;
import com.ntt.authentication.dto.request.RegisterRequest;
import com.ntt.authentication.dto.request.UserUpdateRequest;
import com.ntt.authentication.dto.request.WorkspaceCreationRequest;
import com.ntt.authentication.dto.response.UserResponse;
import com.ntt.authentication.exception.AppException;
import com.ntt.authentication.exception.ErrorCode;
import com.ntt.authentication.mapper.ProfileMapper;
import com.ntt.authentication.mapper.UserMapper;
import com.ntt.authentication.producer.RabbitMQProducer;
import com.ntt.authentication.repository.RoleRepository;
import com.ntt.authentication.repository.UserRepository;
import com.ntt.authentication.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    UserRepository userRepository;
    ProfileClient profileClient;
    ProfileMapper profileMapper;
    RabbitMQProducer rabbitMQProducer;

    public UserResponse register(RegisterRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<Role> roles = new HashSet<>();
        roleRepository.findById(PredefinedRole.USER_ROLE).ifPresent(roles::add);

        user.setRoles(roles);

        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException exception){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        var profileRequest = profileMapper.toProfileCreationRequest(request);
        profileRequest.setUserId(user.getId());

        rabbitMQProducer.sendMessage("profile.created", profileRequest);

        var workspaceRequest = WorkspaceCreationRequest.builder()
                .userId(user.getId())
                .name("Workspace của " + request.getFirstName())
                .description("Không gian làm việc cá nhân mặc định")
                .build();

        rabbitMQProducer.sendMessage("workspace.created", workspaceRequest);

//        var profileResponse = profileClient.createProfile(profileRequest);

        return userMapper.toUserResponse(user);
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByEmail(name).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var roles = roleRepository.findAllById(request.getRoles());
        user.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse getUser(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
    }
}
