package com.springboot.lookoutside.oauth.service;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.springboot.lookoutside.domain.User;
import com.springboot.lookoutside.oauth.entity.ProviderType;
import com.springboot.lookoutside.oauth.entity.RoleType;
import com.springboot.lookoutside.oauth.entity.UserPrincipal;
import com.springboot.lookoutside.oauth.exception.OAuthProviderMissMatchException;
import com.springboot.lookoutside.oauth.info.OAuth2UserInfo;
import com.springboot.lookoutside.oauth.info.OAuth2UserInfoFactory;
import com.springboot.lookoutside.oauth.repository.AuthUserRepository;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AuthUserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user = super.loadUser(userRequest);

        try {
            return this.process(userRequest, user);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User user) {
        ProviderType providerType = ProviderType.valueOf(userRequest.getClientRegistration().getRegistrationId().toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
		
        User savedUser = userRepository.findByUseId(userInfo.getUseId());
       
        if (savedUser != null) {
            if (providerType != savedUser.getProviderType()) {
            	System.out.println(providerType);
                throw new OAuthProviderMissMatchException(
                        "이미 SNS " + providerType +
                        " 계정으로 가입했습니다. SNS " + savedUser.getProviderType() + " 계정으로 로그인 해주세요."
                );
            }
            updateUser(savedUser, userInfo);
        } else {
            savedUser = createUser(userInfo, providerType);
        }

        return UserPrincipal.create(savedUser, user.getAttributes());
    }

    private User createUser(OAuth2UserInfo userInfo, ProviderType providerType) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = new User(
                userInfo.getUseId(),
                userInfo.getUseName(),
                providerType+""+new Date().getTime(),
                userInfo.getUseEmail(),
                0,
                providerType,
                RoleType.USER,
                now
        );

        return userRepository.saveAndFlush(user);
    }

    private User updateUser(User user, OAuth2UserInfo userInfo) {
        if (userInfo.getUseName() != null && !user.getUseName().equals(userInfo.getUseName())) {
            user.setUseName(userInfo.getUseName());
        }
        return user;
    }
}

