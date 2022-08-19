package com.springboot.lookoutside.oauth.info.impl;

import java.util.Map;

import com.springboot.lookoutside.oauth.info.OAuth2UserInfo;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo {

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

	@Override
	public String getUseId() {
		
		return (String) attributes.get("sub");
	}

	@Override
	public String getUseName() {
		
		return (String) attributes.get("name");
	}

	@Override
	public String getUseEmail() {
		
		return (String) attributes.get("email");
	}
}

