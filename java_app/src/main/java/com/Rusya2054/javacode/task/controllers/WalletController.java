package com.Rusya2054.javacode.task.controllers;

import com.Rusya2054.javacode.task.exceptions.InsufficientBalanceException;
import com.Rusya2054.javacode.task.exceptions.ValidationInputDataException;
import com.Rusya2054.javacode.task.exceptions.WalletNotFoundException;
import com.Rusya2054.javacode.task.models.enums.OperationType;
import com.Rusya2054.javacode.task.models.Wallet;
import com.Rusya2054.javacode.task.services.WalletService;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class WalletController {

    @Value("${spring.application.numbers.output.scale}")
    private Integer numbersOutputScale;

    private final WalletService walletService;
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("wallet")
    public ResponseEntity<Map<String, String>> startOperation(@RequestBody OperationRequestBody requestBody){
        try {
            requestBody.validate();
            walletService.walletOperate(requestBody.walletUUID, requestBody.operationType, requestBody.amount);
            return ResponseEntity.ok().body(Map.of("message", "the operation was completed successfully"));
        } catch (WalletNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (InsufficientBalanceException | ValidationInputDataException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        } catch (DataAccessException | PersistenceException ex) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Internal server error"));
        }
    }

    @GetMapping("wallets/{uuid}")
    public ResponseEntity<Map<String, String>> getWalletBalance(@PathVariable("uuid") String walletUUID) {
        try {
            Wallet wallet = walletService.getWalletByUUID(walletUUID);
            return ResponseEntity.ok(
                    Map.of("balance", wallet.getBalance().setScale(numbersOutputScale, RoundingMode.HALF_UP).toPlainString()));
        } catch (WalletNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (DataAccessException | PersistenceException ex) {
            return ResponseEntity.internalServerError().body(Map.of("message", "Internal server error"));
        }
    }
    private static class OperationRequestBody{
        @JsonProperty("valletId")
        private String walletUUID;
        @JsonProperty("operationType")
        private OperationType operationType;
        @JsonProperty("amount")
        private BigDecimal amount;

        private void validate() throws ValidationInputDataException{
            if (walletUUID == null) {
                throw new ValidationInputDataException("valletId", "Id of wallet is null");
            }
            if (operationType == null) {
                throw new ValidationInputDataException("operationType", "Operation type is empty. The operation type may be DEPOSIT or WITHDRAW");
            }
            if (amount == null || amount.compareTo(BigDecimal.valueOf(0)) <= 0) {
                throw new ValidationInputDataException("amount", "Amount must be above zero");
            }
        }
    }
}
