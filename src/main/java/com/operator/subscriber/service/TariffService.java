package com.operator.subscriber.service;

import com.operator.subscriber.payload.request.TariffRequest;
import com.operator.subscriber.payload.response.TariffResponse;

import java.util.List;

public interface TariffService {
    TariffResponse create(TariffRequest request);

    List<TariffResponse> listActive();

    TariffResponse getById(long id);
}

