package com.ntt.taskmanagement.user;

import com.ntt.taskmanagement.user.dto.CreateUserCommand;

public interface UserModuleApi {
    boolean isEmailExisted(String email);
    void createUser(CreateUserCommand command);
}