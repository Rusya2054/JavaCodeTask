package com.Rusya2054.javacode.task.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OperationType {
    DEPOSIT, WITHDRAW;

    @JsonCreator
    public static OperationType fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("OperationType cannot be null");
        }
        String normalizedValue = value.trim().toUpperCase();
        for (OperationType type : values()) {
            if (type.name().equals(normalizedValue)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown OperationType: " + value);
    }
}
