package com.springboot.lookoutside.oauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.springboot.lookoutside.domain.UserRefreshToken;

@Repository
public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    UserRefreshToken findByUseId(String useId);
    UserRefreshToken findByUseIdAndRefreshToken(String useId, String refreshToken);
}