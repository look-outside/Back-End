package com.springboot.lookoutside.oauth.exception;

public class TokenValidFailedException extends RuntimeException {

    public TokenValidFailedException() {
        super("토큰 발행에 실패했습니다.");
    }

    private TokenValidFailedException(String message) {
        super(message);
    }
}
