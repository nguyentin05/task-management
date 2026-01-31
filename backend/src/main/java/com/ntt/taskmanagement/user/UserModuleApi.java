package com.ntt.taskmanagement.user;

import com.ntt.taskmanagement.user.dto.CreateUserCommand;
import com.ntt.taskmanagement.user.dto.UserAuth;

import java.util.Optional;

public interface UserModuleApi {
    boolean isEmailExisted(String email);
    void createUser(CreateUserCommand command);
    Optional<UserAuth> getUserByEmail(String email);
}