package com.Rusya2054.javacode.task.models;

import com.Rusya2054.javacode.task.configurations.BigDecimalOperationConfigurations;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "wallets_tb")
public final class Wallet {
    public Wallet(){}
    public Wallet(String uuid, BigDecimal balance){
        this.uuid = uuid;
        this.balance = balance.setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE);;
    }
    @Id
    @Column(name = "id", length = 36)
    private String uuid;

    @Column(name = "balance", scale = 4, precision = 19, nullable = false)
    private BigDecimal balance;

    public String getUUID() {
        return uuid;
    }
    public BigDecimal getBalance(){
        return balance;
    }

}
