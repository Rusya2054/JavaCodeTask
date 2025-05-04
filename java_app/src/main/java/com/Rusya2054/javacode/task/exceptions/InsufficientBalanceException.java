package com.Rusya2054.javacode.task.exceptions;

import java.io.IOException;

public class InsufficientBalanceException extends IOException {
    @Override
    public String getMessage() {
        return "Insufficient funds to perform the operation";
    }
}
