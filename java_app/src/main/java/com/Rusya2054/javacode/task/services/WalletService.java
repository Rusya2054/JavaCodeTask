package com.Rusya2054.javacode.task.services;

import com.Rusya2054.javacode.task.configurations.PrimaryDataBaseConfig;
import com.Rusya2054.javacode.task.exceptions.InsufficientBalanceException;
import com.Rusya2054.javacode.task.exceptions.WalletNotFoundException;
import com.Rusya2054.javacode.task.models.WalletOperationHistory;
import com.Rusya2054.javacode.task.models.enums.OperationType;
import com.Rusya2054.javacode.task.models.Wallet;
import com.Rusya2054.javacode.task.models.dto.WalletDto;
import com.Rusya2054.javacode.task.repositories.WalletOperationRepository;
import com.Rusya2054.javacode.task.repositories.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class WalletService {
    private static final Logger log = LoggerFactory.getLogger(WalletService.class);
    private final WalletRepository walletRepository;
    private final WalletOperationRepository walletOperationRepository;

    public WalletService(WalletRepository walletRepository, WalletOperationRepository walletOperationRepository) {
        this.walletRepository = walletRepository;
        this.walletOperationRepository = walletOperationRepository;
    }

    public Wallet getWalletByUUID(String uuid) throws WalletNotFoundException {
        return walletRepository.getWalletByUUID(uuid).orElseThrow(()-> new WalletNotFoundException(uuid));
    }

    @Transactional(
            transactionManager = PrimaryDataBaseConfig.TRANSACTION_MANAGER,
            rollbackFor = {WalletNotFoundException.class, InsufficientBalanceException.class},
            isolation = Isolation.READ_COMMITTED
    )
    public void walletOperate(String uuid, OperationType operationType, BigDecimal amount) throws WalletNotFoundException, InsufficientBalanceException {
        Wallet wallet = getWalletByUUID(uuid);
        WalletDto walletDto = new WalletDto(wallet.getUUID(), wallet.getBalance());
        switch (operationType) {
            case DEPOSIT -> walletDto =  walletDto.depositBalance(amount);
            case WITHDRAW -> walletDto = walletDto.withdrawBalance(amount);
        }
        Wallet updatedWallet = walletRepository.save(new Wallet(walletDto.getUUID(), walletDto.getBalance()));
        WalletOperationHistory history = walletOperationRepository.save(new WalletOperationHistory(wallet.getUUID(), operationType, amount));
        log.info("Wallet with walletUUID: {} is updated", updatedWallet.getUUID());
        log.info("Operation: {} is saved with walletUUID: {}", history.getId(), updatedWallet.getUUID());
    }
}
