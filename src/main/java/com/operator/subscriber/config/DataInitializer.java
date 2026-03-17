package com.operator.subscriber.config;

import com.operator.subscriber.entity.AppUser;
import com.operator.subscriber.entity.Role;
import com.operator.subscriber.repository.AppUserRepository;
import com.operator.subscriber.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository, AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_ADMIN")));
        Role subscriberRole = roleRepository.findByName("ROLE_SUBSCRIBER")
                .orElseGet(() -> roleRepository.save(new Role(null, "ROLE_SUBSCRIBER")));

        // Ensure admin exists and password matches current policy (BCrypt)
        AppUser admin = appUserRepository.findByUsername("admin").orElse(null);
        if (admin == null) {
            admin = AppUser.builder()
                    .username("admin")
                    .email("admin@operator.kg")
                    .password(passwordEncoder.encode("admin123"))
                    .enabled(true)
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();
            appUserRepository.save(admin);
        } else if (!passwordEncoder.matches("admin123", admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            admin.setRoles(new HashSet<>(Set.of(adminRole)));
            appUserRepository.save(admin);
        }

        // Ensure roles table always has both roles referenced
        if (subscriberRole.getId() == null) {
            roleRepository.save(subscriberRole);
        }
    }
}

