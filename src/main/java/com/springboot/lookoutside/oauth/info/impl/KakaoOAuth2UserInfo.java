package com.springboot.lookoutside.oauth.info.impl;

import java.util.Map;

import com.springboot.lookoutside.oauth.info.OAuth2UserInfo;

public class KakaoOAuth2UserInfo extends OAuth2UserInfo {

	public KakaoOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUseId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getUseName() {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");

        if (properties == null) {
            return null;
        }

        return (String) properties.get("nickname");
    }

    @Override
    public String getUseEmail() {
        return (String) attributes.get("account_email");
    }

}

