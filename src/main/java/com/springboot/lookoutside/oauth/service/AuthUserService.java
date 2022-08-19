package com.springboot.lookoutside.oauth.service;

import org.springframework.stereotype.Service;

import com.springboot.lookoutside.domain.User;
import com.springboot.lookoutside.oauth.repository.AuthUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthUserService {
    private final AuthUserRepository userRepository;

    public User getUser(String useId) {
        return userRepository.findByUseId(useId);
    }
}
