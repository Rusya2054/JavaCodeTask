package com.Rusya2054.javacode.task.exceptions;

import java.io.IOException;


public class ValidationInputDataException extends IOException {
    private final String field;
    private final String description;
    public ValidationInputDataException(String field, String description){
        this.field = field;
        this.description = description;
    }

    @Override
    public String getMessage(){
        return String.format("Incorrect: '%s'. %s", field, description);
    }
}
