package com.keteso.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.keteso.responses.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.System.currentTimeMillis;
import static java.util.stream.Collectors.toList;

@Component
@Slf4j
public class TokenProvider {
    private static final String AUTHORITIES = "authorities";
    private static final String KCB_GROUP_BSS = "KCB_GROUP_BSS";
    private static final String USER_MANAGEMENT_SERVICE = "USER_MANAGEMENT_SERVICE";
    private static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified!";

    @Value("${jwt.access-token-expiration-time}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;


    @Value("${jwt.secret}")
    private String secret;

    public String createAccessToken(UserPrincipal userPrincipal) {
        return JWT.create().withIssuer(KCB_GROUP_BSS).withAudience(USER_MANAGEMENT_SERVICE).withIssuedAt(new Date()).withSubject(userPrincipal.getUsername()).withArrayClaim(AUTHORITIES, getClaimsFromUser(userPrincipal))
                .withExpiresAt(new Date(currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    public String getSubject(String token, HttpServletRequest request) {
        try {
            return getJWTVerifier().verify(token).getSubject();
        } catch (TokenExpiredException exception) {
            request.setAttribute("expiredMessage", exception.getMessage());
            throw exception;
        } catch (InvalidClaimException exception) {
            request.setAttribute("invalidClaim", exception.getMessage());
            throw exception;
        } catch (Exception exception) {
            throw exception;
        }
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return Arrays.stream(claims).map(SimpleGrantedAuthority::new).collect(toList());
    }

    /* To be used in filter class :: */
    public Authentication getAuthentication(String email, List<GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(email, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    public boolean isTokenValid(String email, String token) {
        try {
            JWTVerifier verifier = getJWTVerifier();
            return StringUtils.isNotEmpty(email) && !isTokenExpired(verifier, token);
        } catch (Exception e) {
            log.error("Error " + e.getLocalizedMessage());
            return false;
        }
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJWTVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    private String[] getClaimsFromUser(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }

    private JWTVerifier getJWTVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(KCB_GROUP_BSS).build();
        } catch (JWTVerificationException ex) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }
}
