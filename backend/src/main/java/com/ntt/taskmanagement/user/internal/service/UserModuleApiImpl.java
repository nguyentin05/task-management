package com.ntt.taskmanagement.user.internal.service;

import com.ntt.taskmanagement.user.UserModuleApi;
import com.ntt.taskmanagement.user.dto.CreateUserCommand;
import com.ntt.taskmanagement.user.internal.domain.User;
import com.ntt.taskmanagement.user.internal.domain.UserRole;
import com.ntt.taskmanagement.user.internal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class UserModuleApiImpl implements UserModuleApi {
    private final UserRepository userRepository;

    @Override
    public boolean isEmailExisted(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void createUser(CreateUserCommand command) {
        User user = User.builder()
                .email(command.getEmail())
                .password(command.getPassword())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .role(UserRole.USER)
                .isActive(true)
                .build();

        userRepository.save(user);
    }
}
