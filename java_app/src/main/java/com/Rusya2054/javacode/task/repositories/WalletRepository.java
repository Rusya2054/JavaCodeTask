package com.Rusya2054.javacode.task.repositories;

import com.Rusya2054.javacode.task.configurations.PrimaryDataBaseConfig;
import com.Rusya2054.javacode.task.models.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    @Transactional(
            transactionManager = PrimaryDataBaseConfig.TRANSACTION_MANAGER)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = """
            SELECT w FROM Wallet w WHERE w.uuid = :uuid
            """)
    Optional<Wallet> getWalletByUUID(@Param("uuid") String uuid);


}
