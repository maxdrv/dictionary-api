package com.home.dictionary.security;

import com.home.dictionary.exception.ThisShouldNeverHappen;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.sql.Date;
import java.time.Clock;

import static java.util.Date.from;

@RequiredArgsConstructor

@Service
public class JwtProvider {

    private final Clock clock;

    private KeyStore keyStore;

    @Value("${jwt.expiration.time.access}")
    private Long accessTokenExpirationInMillis;

    @Value("${jwt.expiration.time.access}")
    private Long refreshTokenExpirationInMillis;

    @Value("${jwt.keystore.password}")
    private String jwtKeyStorePassword;

    @Value("${jwt.keystore.path}")
    private String jwtKeyStorePath;

    @Value("${jwt.keystore.alias}")
    private String jwtKeyStoreAlias;

    @PostConstruct
    public void init() {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream(jwtKeyStorePath);
            keyStore.load(resourceAsStream, jwtKeyStorePassword.toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new ThisShouldNeverHappen("Exception occurred while loading keystore", e);
        }
    }

    public String generateAccessToken(String username) {
        return generateToken(
                username,
                getPrivateKey(),
                accessTokenExpirationInMillis
        );
    }

    public String generateRefreshToken(String username) {
        return generateToken(
                username,
                getPrivateKey(),  // TODO: separate key for refresh token???
                refreshTokenExpirationInMillis
        );
    }

    private String generateToken(String username, PrivateKey privateKey, Long expirationInMillis) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(from(clock.instant()))
                .signWith(privateKey)
                .setExpiration(Date.from(clock.instant().plusMillis(expirationInMillis)))
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            return (PrivateKey) keyStore.getKey(jwtKeyStoreAlias, jwtKeyStorePassword.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new ThisShouldNeverHappen("Exception occurred while retrieving private key from keystore", e);
        }
    }

    private PublicKey getPublicKey() {
        try {
            return keyStore.getCertificate(jwtKeyStoreAlias).getPublicKey();
        } catch (KeyStoreException e) {
            throw new ThisShouldNeverHappen("Exception occurred while retrieving public key from keystore", e);
        }
    }

    public boolean validateToken(String jwt) {
        try {
            Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException ex) {
            return false;
        }
    }

    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

}
