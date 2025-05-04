package com.Rusya2054.javacode.task.exceptions;

import java.io.IOException;

public class WalletNotFoundException extends IOException {
    private final String walletUUID;
    public WalletNotFoundException(String walletUUID) {
        this.walletUUID = walletUUID;
    }

    @Override
    public String getMessage() {
        return String.format("UUID of wallet: '%s' not found", this.walletUUID);
    }
}
