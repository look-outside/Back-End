package com.springboot.lookoutside.oauth.info.impl;

import java.util.Map;

import com.springboot.lookoutside.oauth.info.OAuth2UserInfo;

public class NaverOAuth2UserInfo extends OAuth2UserInfo{

	public NaverOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getUseId() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        return (String) response.get("id");
    }

    @Override
    public String getUseName() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        return (String) response.get("name");
    }

    @Override
    public String getUseEmail() {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        if (response == null) {
            return null;
        }

        return (String) response.get("email");
    }

	
}
