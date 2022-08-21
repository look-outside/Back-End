package com.springboot.lookoutside.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.lookoutside.domain.AuthReqModel;
import com.springboot.lookoutside.domain.User;
import com.springboot.lookoutside.oauth.entity.UserPrincipal;
import com.springboot.lookoutside.oauth.repository.AuthUserRepository;

import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {
	
	private final AuthenticationManager authenticationManager; 
	private final AuthUserRepository userRepository;
	//private final BCryptPasswordEncoder encoder;
	private final PasswordEncoder encoder;

	
    @PostMapping("/testlogin")
    public Authentication customLoginProcess(@RequestBody AuthReqModel authReqModel){
    	System.out.println("TEST LOGIN");
    	
    	User persistance = userRepository.findByUseId(authReqModel.getUseId());
    	
    	System.out.println(authReqModel.getUsePw());
    	System.out.println(persistance.getUsePw());

    	System.out.println(encoder.matches(authReqModel.getUsePw(), persistance.getUsePw()));
    	
        // 아이디와 패스워드로, Security 가 알아 볼 수 있는 token 객체로 변경한다.
    	try {

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(persistance.getUseId(), authReqModel.getUsePw());
            
            System.out.println("TEST LOGIN2");
            System.out.println(authenticationToken.getCredentials().toString());
            
            Authentication authentication =
            		authenticationManager.authenticate(authenticationToken);
            System.out.println(authentication);
            System.out.println("TEST LOGIN3");
            
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            System.out.println(userPrincipal.getUseId());
            
            return authentication;

        } catch (BadCredentialsException e) {
             e.printStackTrace();
        }
    	  return null;
    }
    
}
