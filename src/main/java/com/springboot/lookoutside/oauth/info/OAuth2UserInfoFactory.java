package com.springboot.lookoutside.oauth.info;

import java.util.Map;

import com.springboot.lookoutside.oauth.entity.ProviderType;
import com.springboot.lookoutside.oauth.info.impl.GoogleOAuth2UserInfo;
import com.springboot.lookoutside.oauth.info.impl.KakaoOAuth2UserInfo;
import com.springboot.lookoutside.oauth.info.impl.NaverOAuth2UserInfo;

public class OAuth2UserInfoFactory {
    public static OAuth2UserInfo getOAuth2UserInfo(ProviderType providerType, Map<String, Object> attributes) {
        switch (providerType) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case NAVER: return new NaverOAuth2UserInfo(attributes);
            case KAKAO: return new KakaoOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("옳지않은 소셜 로그인.");
        }
    }
}
