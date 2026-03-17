package com.operator.subscriber.service;

import com.operator.subscriber.payload.request.SubscriberRequest;
import com.operator.subscriber.payload.response.SubscriberResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface SubscriberService {
    SubscriberResponse create(SubscriberRequest request);

    SubscriberResponse getById(long id);

    SubscriberResponse getByMsisdn(String msisdn);

    List<SubscriberResponse> listAll();

    SubscriberResponse update(long id, SubscriberRequest request);

    SubscriberResponse changeTariff(long id, long tariffId);

    SubscriberResponse topUp(long id, BigDecimal amount, String description);

    SubscriberResponse charge(long id, BigDecimal amount, String description);

    SubscriberResponse uploadPhoto(long id, MultipartFile file);

    Resource getPhoto(long id);

    void delete(long id);
}

