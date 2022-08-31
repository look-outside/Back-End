package com.springboot.lookoutside.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.springboot.lookoutside.common.ApiResponse;
import com.springboot.lookoutside.config.properties.AppProperties;
import com.springboot.lookoutside.domain.AuthReqModel;
import com.springboot.lookoutside.domain.User;
import com.springboot.lookoutside.domain.UserRefreshToken;
import com.springboot.lookoutside.oauth.entity.RoleType;
import com.springboot.lookoutside.oauth.entity.UserPrincipal;
import com.springboot.lookoutside.oauth.repository.AuthUserRepository;
import com.springboot.lookoutside.oauth.repository.UserRefreshTokenRepository;
import com.springboot.lookoutside.oauth.token.AuthToken;
import com.springboot.lookoutside.oauth.token.AuthTokenProvider;
import com.springboot.lookoutside.utils.CookieUtil;
import com.springboot.lookoutside.utils.HeaderUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
	private final PasswordEncoder encoder;

    private final AuthUserRepository userRepository;
    
    private final static long THREE_DAYS_MSEC = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";

    @PostMapping("/login")
    public ApiResponse login(HttpServletRequest request, HttpServletResponse response, @RequestBody AuthReqModel authReqModel) {
    	
    	User persistance = userRepository.findByUseId(authReqModel.getUseId());
    	
    	System.out.println("로그인 테스트");
    	System.out.println(authReqModel.getUsePw());
    	System.out.println(persistance.getUsePw());
    	System.out.println(persistance.getProviderType());
    	System.out.println(encoder.matches(authReqModel.getUsePw(), persistance.getUsePw()));
    	
    	
    		System.out.println("로그인 테스트2");
    		UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(persistance.getUseId(), authReqModel.getUsePw());
    		System.out.println(authenticationToken);
    		System.out.println("로그인 테스트3");
    		Authentication authentication = authenticationManager.authenticate(authenticationToken);
    		
    		System.out.println("로그인 테스트4");
            String useId = authReqModel.getUseId();
            
            User user = userRepository.findByUseId(useId);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Date now = new Date();
            AuthToken accessToken = tokenProvider.createAuthToken(
                    useId,
                    user.getUseNo(),
                    user.getUseNick(),
                    ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
                    new Date(now.getTime() + appProperties.getAuth().getTokenExpiry()),
                    user.getSnsNick()
            );

            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
            AuthToken refreshToken = tokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),
                    new Date(now.getTime() + refreshTokenExpiry)
            );

            // userId refresh token 으로 DB 확인
            UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUseId(useId);
            if (userRefreshToken == null) {
                // 없는 경우 새로 등록
                userRefreshToken = new UserRefreshToken(useId, refreshToken.getToken());
                userRefreshTokenRepository.saveAndFlush(userRefreshToken);
            } else {
                // DB에 refresh 토큰 업데이트
                userRefreshToken.setRefreshToken(refreshToken.getToken());
            }

            int cookieMaxAge = (int) refreshTokenExpiry / 60;
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
            return ApiResponse.success("token", accessToken.getToken());       

    }

    @GetMapping("/refresh")
    public ApiResponse refreshToken (HttpServletRequest request, HttpServletResponse response) {
        // access token 확인
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
        if (!authToken.validate()) {
            return ApiResponse.invalidAccessToken();
        }

        // expired access token 인지 확인
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            return ApiResponse.notExpiredTokenYet();
        }

        String useId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        // refresh token
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

        if (authRefreshToken.validate()) {
            return ApiResponse.invalidRefreshToken();
        }

        // userId refresh token 으로 DB 확인
        UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUseIdAndRefreshToken(useId, refreshToken);
        if (userRefreshToken == null) {
            return ApiResponse.invalidRefreshToken();
        }
        User user = userRepository.findByUseId(useId);
        Date now = new Date();
        AuthToken newAccessToken = tokenProvider.createAuthToken(
                useId,
                user.getUseNo(),
                user.getUseNick(),
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry()),
                user.getSnsNick()
        );

        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        if (validTime <= THREE_DAYS_MSEC) {
            // refresh 토큰 설정
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            authRefreshToken = tokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),
                    new Date(now.getTime() + refreshTokenExpiry)
            );

            // DB에 refresh 토큰 업데이트
            userRefreshToken.setRefreshToken(authRefreshToken.getToken());

            int cookieMaxAge = (int) refreshTokenExpiry / 60;
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
        }

        return ApiResponse.success("token", newAccessToken.getToken());
    }
}
