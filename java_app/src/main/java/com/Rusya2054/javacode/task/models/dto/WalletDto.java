package com.Rusya2054.javacode.task.models.dto;

import com.Rusya2054.javacode.task.configurations.BigDecimalOperationConfigurations;
import com.Rusya2054.javacode.task.exceptions.InsufficientBalanceException;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class WalletDto {
    private final String uuid;
    private final BigDecimal balance;


    public WalletDto(String uuid, BigDecimal balance) {
        this.uuid = uuid;
        this.balance = balance.setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE);
    }

    public WalletDto depositBalance(BigDecimal amount) {
        BigDecimal scaledAmount = amount.setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE);
        return new WalletDto(uuid, balance.add(scaledAmount).setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE));
    }

    public WalletDto withdrawBalance(BigDecimal amount) throws InsufficientBalanceException {
        BigDecimal scaledAmount = amount.setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE);
        if (balance.compareTo(scaledAmount) < 0) throw new InsufficientBalanceException();
        return new WalletDto(uuid, balance.subtract(scaledAmount).setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE));
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getUUID() {
        return uuid;
    }
}
