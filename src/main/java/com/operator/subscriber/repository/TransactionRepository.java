package com.operator.subscriber.repository;

import com.operator.subscriber.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findBySubscriberIdOrderByCreatedAtDesc(Long subscriberId);
}

