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
    	System.out.println("UserDetailsService Test");
        User user = userRepository.findByUseId(useId);
        if (user == null) {
            throw new UsernameNotFoundException("존재하지 않는 ID");
        }
        System.out.println("Id : " +UserPrincipal.create(user).getName());
        System.out.println("Username : " +UserPrincipal.create(user).getUsername());
        System.out.println("Pw : " + UserPrincipal.create(user).getPassword());
        
        return UserPrincipal.create(user);
    }
}
