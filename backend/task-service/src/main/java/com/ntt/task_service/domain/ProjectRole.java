package com.ntt.task_service.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;

public enum ProjectRole {
    MANAGER,
    MEMBER;

    @JsonCreator
    public static ProjectRole fromValue(String value) {
        for (ProjectRole role : values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new AppException(ErrorCode.PROJECT_ROLE_INVALID);
    }
}
