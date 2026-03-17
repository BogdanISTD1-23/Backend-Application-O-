package com.operator.subscriber.service;

import com.operator.subscriber.payload.request.LoginRequest;
import com.operator.subscriber.payload.request.RegisterRequest;
import com.operator.subscriber.payload.response.LoginResponse;

public interface AuthService {
    LoginResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}

