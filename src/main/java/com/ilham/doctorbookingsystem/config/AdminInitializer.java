package com.ilham.doctorbookingsystem.config;

import com.ilham.doctorbookingsystem.entity.UserEntity;
import com.ilham.doctorbookingsystem.enums.RoleEnum;
import com.ilham.doctorbookingsystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String adminEmail = "admin@gmail.com";

        if (userRepository.findByEmail(adminEmail).isEmpty()) {

            UserEntity admin = UserEntity.builder()
                    .firstName("System")
                    .lastName("Admin")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .phoneNumber("0000000000")
                    .role(RoleEnum.ADMIN)
                    .enabled(true)
                    .createdAt(LocalDateTime.now())
                    .build();

            userRepository.save(admin);

            log.info("Default admin account created");
        } else {
            log.info("Admin account already exists");
        }
    }
}