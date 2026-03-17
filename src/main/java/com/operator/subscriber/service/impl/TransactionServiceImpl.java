package com.operator.subscriber.service.impl;

import com.operator.subscriber.dao.TransactionJdbcTemplateDao;
import com.operator.subscriber.entity.Transaction;
import com.operator.subscriber.payload.response.TransactionResponse;
import com.operator.subscriber.repository.SubscriberRepository;
import com.operator.subscriber.service.AbstractBaseService;
import com.operator.subscriber.service.TransactionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionServiceImpl extends AbstractBaseService implements TransactionService {
    private final TransactionJdbcTemplateDao transactionJdbcTemplateDao;
    private final SubscriberRepository subscriberRepository;

    public TransactionServiceImpl(TransactionJdbcTemplateDao transactionJdbcTemplateDao, SubscriberRepository subscriberRepository) {
        this.transactionJdbcTemplateDao = transactionJdbcTemplateDao;
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public List<TransactionResponse> listBySubscriberId(long subscriberId) {
        var subscriber = requireEntity(subscriberRepository.findById(subscriberId), "Subscriber not found: " + subscriberId);
        String msisdn = subscriber.getMsisdn();

        return transactionJdbcTemplateDao.findBySubscriberId(subscriberId).stream()
                .map(tx -> toResponse(tx, subscriberId, msisdn))
                .toList();
    }

    private TransactionResponse toResponse(Transaction tx, long subscriberId, String msisdn) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .subscriberId(subscriberId)
                .subscriberMsisdn(msisdn)
                .amount(tx.getAmount())
                .type(tx.getType() == null ? null : tx.getType().name())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}

