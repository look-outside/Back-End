package com.springboot.lookoutside.oauth.token;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {

    @Getter
    private final String token;
    private final Key key;

    private static final String AUTHORITIES_KEY = "role";

    AuthToken(String id, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, expiry);
    }

    AuthToken(String id, String role, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, role, expiry);
    }
    
    AuthToken(String id, int useNo, String role, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, useNo, role, expiry);
    }
    
    AuthToken(String id, int useNo, String useNick, String role, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, useNo, useNick, role, expiry);
    }
    
    AuthToken(String id, int useNo, String useNick, String role, Date expiry, int snsNick ,Key key) {
        this.key = key;
        this.token = createAuthToken(id, useNo, useNick, role, expiry, snsNick);
    }

    private String createAuthToken(String id, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    private String createAuthToken(String id, String role, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .claim(AUTHORITIES_KEY, role)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }
    
    private String createAuthToken(String id, int useNo, String role, Date expiry) {
    	System.out.println("권한" + role);
        return Jwts.builder()
                .setSubject(id)
                .claim("useNo", useNo)
                .claim(AUTHORITIES_KEY, role)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }
    
    private String createAuthToken(String id, int useNo, String useNick ,String role, Date expiry) {
    	System.out.println("권한" + role);
        return Jwts.builder()
                .setSubject(id)
                .claim("useNo", useNo)
                .claim("useNick", useNick)
                .claim(AUTHORITIES_KEY, role)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    private String createAuthToken(String id, int useNo, String useNick ,String role, Date expiry, int snsNick) {
    	System.out.println("권한" + role);
        return Jwts.builder()
                .setSubject(id)
                .claim("useNo", useNo)
                .claim("useNick", useNick)
                .claim(AUTHORITIES_KEY, role)
                .claim("snsNick", snsNick)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }
    
    public boolean validate() {
        return this.getTokenClaims() != null;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public Claims getExpiredTokenClaims() {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            return e.getClaims();
        }
        return null;
    }
}