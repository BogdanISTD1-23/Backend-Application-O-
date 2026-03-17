package com.operator.subscriber.service;

import com.operator.subscriber.exception.NotFoundException;

import java.util.Optional;

public abstract class AbstractBaseService {
    protected <T> T requireEntity(Optional<T> maybe, String notFoundMessage) {
        return maybe.orElseThrow(() -> new NotFoundException(notFoundMessage));
    }
}

