package com.operator.subscriber.service.impl;

import com.operator.subscriber.entity.AppUser;
import com.operator.subscriber.entity.Role;
import com.operator.subscriber.exception.ConflictException;
import com.operator.subscriber.payload.request.LoginRequest;
import com.operator.subscriber.payload.request.RegisterRequest;
import com.operator.subscriber.payload.response.LoginResponse;
import com.operator.subscriber.repository.AppUserRepository;
import com.operator.subscriber.repository.RoleRepository;
import com.operator.subscriber.security.JwtTokenProvider;
import com.operator.subscriber.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {
    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(
            AppUserRepository appUserRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider
    ) {
        this.appUserRepository = appUserRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }
        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        Role role = roleRepository.findByName("ROLE_SUBSCRIBER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_SUBSCRIBER")));

        AppUser user = AppUser.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(java.util.Set.of(role))
                .build();

        AppUser saved = appUserRepository.save(user);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        return toLoginResponse(saved, auth, tokenProvider.generateToken(auth));
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        AppUser user = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        return toLoginResponse(user, auth, tokenProvider.generateToken(auth));
    }

    private static LoginResponse toLoginResponse(AppUser user, Authentication auth, String token) {
        List<String> roles = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}

