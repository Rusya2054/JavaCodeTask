package com.Rusya2054.javacode.task.repositories;

import com.Rusya2054.javacode.task.models.WalletOperationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletOperationRepository extends JpaRepository<WalletOperationHistory, Long> {
}
