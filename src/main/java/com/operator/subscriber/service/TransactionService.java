package com.operator.subscriber.service;

import com.operator.subscriber.payload.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    List<TransactionResponse> listBySubscriberId(long subscriberId);
}

