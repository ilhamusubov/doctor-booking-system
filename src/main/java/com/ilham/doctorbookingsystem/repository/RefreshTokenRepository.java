package com.ilham.doctorbookingsystem.repository;


import com.ilham.doctorbookingsystem.entity.RefreshTokenEntity;
import com.ilham.doctorbookingsystem.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    Optional<RefreshTokenEntity> findByUser(UserEntity user);

    Optional<RefreshTokenEntity> findByUserId(Long userId);

    void deleteByUserId(Long userId);

    void deleteByUser(UserEntity user);
}
