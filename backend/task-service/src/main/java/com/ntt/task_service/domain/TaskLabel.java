package com.ntt.task_service.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.ntt.task_service.exception.AppException;
import com.ntt.task_service.exception.ErrorCode;

public enum TaskLabel {
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    BLUE,
    PURPLE,
    PINK,
    GRAY;

    @JsonCreator
    public static TaskLabel fromValue(String value) {
        for (TaskLabel label : values()) {
            if (label.name().equalsIgnoreCase(value)) {
                return label;
            }
        }
        throw new AppException(ErrorCode.LABEL_INVALID);
    }
}
