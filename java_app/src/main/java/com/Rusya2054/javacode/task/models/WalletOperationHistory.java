package com.Rusya2054.javacode.task.models;

import com.Rusya2054.javacode.task.models.enums.OperationType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "wallet_operations_tb", indexes = {@Index(columnList = "id, target_wallet_id", name = "wallet_history_index")})
public final class WalletOperationHistory {
    public WalletOperationHistory() {}

    public WalletOperationHistory(String targetWalletUUID, OperationType operationType, BigDecimal amount) {
        this.targetWalletUUID = targetWalletUUID;
        this.operationType = operationType;
        this.amount = amount;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    private Long createdAt = Instant.now().getEpochSecond();

    @Column(name = "operation_type")
    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @Column(name = "target_wallet_id", length = 36)
    private String targetWalletUUID;

    @Column(name = "amount", scale = 4, precision = 19, nullable = false)
    private BigDecimal amount;

    public Long getId() {
        return id;
    }

    public String getTargetWalletUUID() {
        return targetWalletUUID;
    }

}
