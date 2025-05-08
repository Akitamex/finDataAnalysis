package com.ubm.ubmweb.security.jwt;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.ubm.ubmweb.model.Role;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;

import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.keys.AesKey;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.lang.JoseException;

import java.security.Key;

@Component
public class JwtTokenProvider {

    private SecretKey key;

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    @Value("${jwt.token.secret}")
    private String secret;
	
    @Value("${jwt.token.encryption}")
    private String encryption;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;



    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @PostConstruct
    protected void init() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        key = Keys.hmacShaKeyFor(keyBytes);
        logger.info("Initialized encoded JWT secret key: {}", secret);
    }

    public String createToken(String phone, UUID userId, List<Role> roles) {

        Claims claims = Jwts.claims().setSubject(phone);
        claims.put("roles", getRoleNames(roles));
        claims.put("userId", userId);

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        logger.debug("Using encoded JWT secret key for token creation: {}", secret);

        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
		String encryptedToken = "no token";
		try	{	
			String encryptSecret = encryption;
			byte[] encryptSecretBytes = encryptSecret.getBytes();
			Key key = new AesKey(encryptSecretBytes);
			JsonWebEncryption jwe = new JsonWebEncryption();
			jwe.setPayload(token);
			jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A256KW); 
			jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_256_CBC_HMAC_SHA_512);
			jwe.setKey(key);
			encryptedToken = jwe.getCompactSerialization(); 
		} catch (JoseException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
		return encryptedToken;
    }


    @Autowired
    @Lazy
    private ApplicationContext applicationContext;
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = applicationContext.getBean(UserDetailsService.class)
                .loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    public UUID getUserId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        //UUID userId = claims.get("userId", UUID.class);
        UUID userId = UUID.fromString(claims.get("userId", String.class));
        //Number userId = claims.get("userId", Number.class); // Извлекаем как Number, чтобы избежать ClassCastException
        return userId;// != null ? userId.longValue() : null; // Преобразовываем Number в Long, если он не null
    }
    public String getUsername(String token) {
        logger.debug("Decoding token with secret key: {}", secret);
        JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        return jwtParser.parseClaimsJws(token).getBody().getSubject();
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
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("Invalid JWT token");
        }
	}

    public boolean validateToken(String token) {
        try {
            logger.debug("Using encoded JWT secret key for token validation: {}", secret);
            JwtParser jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
            Jws<Claims> claims = jwtParser.parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                logger.info("Token is expired");
                return false;
            }

            return true;
        } catch (JwtException e) {
            logger.error("JWT token validation error: {}", e.getMessage());
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("Invalid JWT token");
        }


    }

    private List<String> getRoleNames(List<Role> userRoles) {
        List<String> result = new ArrayList<>();

        userRoles.forEach(role -> {
            result.add(role.getName());
        });

        return result;
    }
}
