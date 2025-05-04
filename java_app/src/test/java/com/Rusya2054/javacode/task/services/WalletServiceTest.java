package com.Rusya2054.javacode.task.services;

import com.Rusya2054.javacode.task.JavaCodeTaskApplication;
import com.Rusya2054.javacode.task.configurations.BigDecimalOperationConfigurations;
import com.Rusya2054.javacode.task.exceptions.InsufficientBalanceException;
import com.Rusya2054.javacode.task.exceptions.WalletNotFoundException;
import com.Rusya2054.javacode.task.models.Wallet;
import com.Rusya2054.javacode.task.models.enums.OperationType;
import com.Rusya2054.javacode.task.repositories.WalletRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

@SpringBootTest(
        classes = {JavaCodeTaskApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class
})
@ActiveProfiles("test")
class WalletServiceTest {
    @Autowired
    private WalletService service;

    @Autowired
    WalletRepository walletRepository;

    @Test
    void getWalletByEmptyUUIDTest(){
        String uuid = "";
        Assertions.assertThrows(WalletNotFoundException.class, () ->{
            service.getWalletByUUID(uuid);
        });
    }
    @Test
    void getWalletByWhiteSpacesUUIDTest(){
        String uuid = " ";
        Assertions.assertThrows(WalletNotFoundException.class, () ->{
            service.getWalletByUUID(uuid);
        });
    }

    @Test
    void getWalletByRandomGeneratedUUIDTest(){
        String uuid = "123123123123";
        Assertions.assertThrows(WalletNotFoundException.class, () ->{
            service.getWalletByUUID(uuid);
        });
    }

    @Test
    void savingDeletingWalletRepositoryTest() throws WalletNotFoundException {
        String uuid = UUID.randomUUID().toString();
        Wallet w = new Wallet(uuid, BigDecimal.valueOf(0));
        walletRepository.save(w);
        AtomicReference<Wallet> walletAtomicReference = new AtomicReference<>();
        Assertions.assertDoesNotThrow(() -> {
            walletAtomicReference.set(service.getWalletByUUID(uuid));
        });
        Wallet wallet = walletAtomicReference.get();
        Assertions.assertNotNull(wallet);
        walletRepository.deleteById(uuid);
        Assertions.assertThrows(WalletNotFoundException.class, () ->{
            service.getWalletByUUID(uuid);
        });
    }

    @Test
    void walletDepositOperate() throws WalletNotFoundException, InsufficientBalanceException {
        String uuid = "test-deposit-uuid";
        Wallet w = new Wallet(uuid, BigDecimal.valueOf(0));
        walletRepository.save(w);
        Integer numberOfOperations = 1000;
        BigDecimal amount = BigDecimal.valueOf((double) 1/numberOfOperations);
        for (int i=0; i<numberOfOperations ; i++) {
            service.walletOperate(uuid, OperationType.DEPOSIT, amount);
        }
        Wallet dbWallet = service.getWalletByUUID(uuid);
        Assertions.assertTrue(dbWallet.getBalance().compareTo(BigDecimal.valueOf(1L)) == 0);
        walletRepository.deleteById(uuid);
        Assertions.assertThrows(WalletNotFoundException.class, () ->{
            service.getWalletByUUID(uuid);
        });
    }

    @Test
    void walletWithdrawOperate() throws WalletNotFoundException {
        String uuid = "test-withdraw-uuid";
        Wallet w = new Wallet(uuid, BigDecimal.valueOf(0));
        walletRepository.save(w);
        Integer numberOfOperations = 10;
        BigDecimal amount = BigDecimal.valueOf((double) 1/numberOfOperations);
        for (int i=0; i<numberOfOperations ; i++) {
            try {
                service.walletOperate(uuid, OperationType.WITHDRAW, amount);
            } catch (InsufficientBalanceException ie) {
                System.out.println(ie.getMessage());
            }
        }
        Wallet dbWallet = service.getWalletByUUID(uuid);
        Assertions.assertTrue(dbWallet.getBalance().compareTo(BigDecimal.valueOf(0L)) == 0);
        walletRepository.deleteById(uuid);
        Assertions.assertThrows(WalletNotFoundException.class, () ->{
            service.getWalletByUUID(uuid);
        });
    }

   
    @Test
    void walletAsyncDepositOperates() throws WalletNotFoundException{
        final Integer numbersOfConcurrentOperations = 1000;
        final Integer numberOfOperations = 10;
        final BigDecimal amount = BigDecimal.valueOf((double) 1/numberOfOperations);
        String[] uuids = IntStream
                .range(0, numbersOfConcurrentOperations)
                .boxed().map(m-> UUID.randomUUID().toString())
                .toArray(String[]::new);
        Arrays.stream(uuids).forEach(uuid -> {
            try {
                walletRepository.save(new Wallet(uuid, BigDecimal.valueOf(0)));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        List<CompletableFuture<Void>> futures = new ArrayList<>(numbersOfConcurrentOperations);
        for (int i = 0; i< numbersOfConcurrentOperations; i++) {
            String uuid = uuids[i];
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->{
                for (int operation = 0; operation < numberOfOperations; operation++){
                    try {
                        service.walletOperate(uuid, OperationType.DEPOSIT, amount);
                    } catch (WalletNotFoundException | InsufficientBalanceException e) {
                        throw new RuntimeException(e);
                    }
                }
            } );
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        for (int i = 0; i< numbersOfConcurrentOperations; i++) {
            Wallet dbWallet = service.getWalletByUUID(uuids[i]);
            Assertions.assertTrue(dbWallet.getBalance().compareTo(BigDecimal.valueOf(1L)) == 0);
        }
        for (int i = 0; i< numbersOfConcurrentOperations; i++) {
            String uuid = uuids[i];
            walletRepository.deleteById(uuid);
            Assertions.assertThrows(WalletNotFoundException.class, () ->{
            service.getWalletByUUID(uuid);
            });
        }
    }

    @Test
    public void transferTest1() throws WalletNotFoundException{
        final Integer numbersOfWallets = 10;
        final Integer numberOfOperations = 10000;
        this.walletAsyncWithdrawAndDepositsOperates(numbersOfWallets, numberOfOperations);
    }

    @Test
    public void transferTwoForTwo() throws WalletNotFoundException{
        final Integer numbersOfWallets = 2;
        final Integer numberOfOperations = 2;
        this.walletAsyncWithdrawAndDepositsOperates(numbersOfWallets, numberOfOperations);
    }

    @Test
    public void transferManyForTo() throws WalletNotFoundException{
        final Integer numbersOfWallets = 2;
        final Integer numberOfOperations = 1000;
        this.walletAsyncWithdrawAndDepositsOperates(numbersOfWallets, numberOfOperations);
    }

    @Test
    public void transferTwoOperationsForManyWallets() throws WalletNotFoundException{
        final Integer numbersOfWallets = 1000;
        final Integer numberOfOperations = 2;
        this.walletAsyncWithdrawAndDepositsOperates(numbersOfWallets, numberOfOperations);
    }

    void walletAsyncWithdrawAndDepositsOperates(Integer numbersOfWallets, Integer numberOfOperations) throws WalletNotFoundException{


        final Random random = new Random(17092000);
        AtomicReference<BigDecimal> totalBalance = new AtomicReference<>(BigDecimal.ZERO.setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE));
        String[] uuids = IntStream
                .range(0, numbersOfWallets)
                .boxed().map(m-> UUID.randomUUID().toString())
                .toArray(String[]::new);
        Arrays.stream(uuids).forEach(uuid -> {
            try {
                // Init of wallets
                BigDecimal walletInitialBalance = BigDecimal.valueOf(random.nextDouble()*1000)
                    .setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE);
                totalBalance.set(totalBalance.get().add(walletInitialBalance));
                walletRepository.save(new Wallet(uuid, walletInitialBalance));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        List<CompletableFuture<Void>> futures = new ArrayList<>(numbersOfWallets);
        for (int i = 0; i< numberOfOperations; i++) {

            String depositRandomUUID = uuids[random.nextInt(uuids.length)];
            String withdrawRandomUUID = uuids[random.nextInt(uuids.length)];
            while (depositRandomUUID.equals(withdrawRandomUUID)) {
                withdrawRandomUUID = uuids[random.nextInt(uuids.length)];
            }
            String finalWithdrawRandomUUID = withdrawRandomUUID;
            BigDecimal operationRandomValue = BigDecimal.valueOf(random.nextDouble()*1000)
                .setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() ->{
                try {
                    service.walletOperate(finalWithdrawRandomUUID, OperationType.WITHDRAW, operationRandomValue);
                    service.walletOperate(depositRandomUUID, OperationType.DEPOSIT, operationRandomValue);
                } catch (WalletNotFoundException | InsufficientBalanceException e) {
                    System.out.println(e.getMessage());
                }
            } );
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        BigDecimal checkBalance = BigDecimal.ZERO.setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE);
        for (int i = 0; i< numbersOfWallets; i++) {
            Wallet dbWallet = service.getWalletByUUID(uuids[i]);
            checkBalance = checkBalance.add(dbWallet.getBalance()).setScale(BigDecimalOperationConfigurations.SCALE, BigDecimalOperationConfigurations.ROUNDING_MODE);

        }
        Assertions.assertTrue(checkBalance.compareTo(totalBalance.get()) == 0);
        for (int i = 0; i< numbersOfWallets; i++) {
            String uuid = uuids[i];
            walletRepository.deleteById(uuid);
        }
    }



}