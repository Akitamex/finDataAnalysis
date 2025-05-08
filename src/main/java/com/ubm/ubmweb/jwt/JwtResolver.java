package com.ubm.ubmweb.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.keys.AesKey;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.lang.JoseException;

import java.security.Key;

@Component
public class JwtResolver {

    @Value("${jwt.token.secret}")
    private String secret;
	
    @Value("${jwt.token.encryption}")
    private String encryption;

	private Key getSigningKey() {
		byte[] keyBytes = this.secret.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        Number userId = claims.get("userId", Number.class);
        return userId != null ? userId.longValue() : null;
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer_")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
	
	public String decryptToken(String token) {
		try {			
			String encryptSecret = encryption;
			byte[] encryptSecretBytes = encryptSecret.getBytes();
			Key key = new AesKey(encryptSecretBytes);
			JsonWebEncryption jwe = new JsonWebEncryption();
			jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A256KW);
			jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512);
			jwe.setKey(key);			
			jwe.setCompactSerialization(token);        
			String decryptedToken = jwe.getPayload();
            return decryptedToken;
        } catch (JoseException e) {
            throw new JwtAuthenticationException("Invalid JWT token");
        }
	}

    public boolean validateToken(String token) {
        try {
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(getSigningKey()).build();
            Jws<Claims> claims = jwtParser.parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        } catch (IllegalArgumentException e) {
            throw new JwtAuthenticationException("Invalid JWT token");
        }


    }
}