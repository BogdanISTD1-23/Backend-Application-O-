package com.operator.subscriber.repository;

import com.operator.subscriber.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    Optional<Subscriber> findByMsisdn(String msisdn);

    boolean existsByMsisdn(String msisdn);
}

