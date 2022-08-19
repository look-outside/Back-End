package com.springboot.lookoutside.oauth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.springboot.lookoutside.domain.User;
import com.springboot.lookoutside.oauth.entity.UserPrincipal;
import com.springboot.lookoutside.oauth.repository.AuthUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String useId) throws UsernameNotFoundException {
        User user = userRepository.findByUseId(useId);
        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 ID");
        }
        return UserPrincipal.create(user);
    }
}
