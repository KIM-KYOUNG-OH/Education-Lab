package com.project.educationLab.domain.auth.util;

import com.project.educationLab.domain.auth.dto.PrincipalDetails;
import com.project.educationLab.domain.user.entity.User;
import com.project.educationLab.domain.user.repository.UserRepository;
import com.project.educationLab.global.util.CookieUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {

    private final Key accessTokenKey;
    private final Key refreshTokenKey;
    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60;
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7;
    private final UserRepository userRepository;

    public TokenProvider(@Value("${jwt.access-token-key}") String SECRET_KEY, @Value("${jwt.refresh-token-key}") String REFRESH_TOKEN_KEY, UserRepository userRepository) {
        this.userRepository = userRepository;
        byte[] encodedAccessTokenKey = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes()).getBytes();
        this.accessTokenKey = Keys.hmacShaKeyFor(encodedAccessTokenKey);
        byte[] encodedRefreshTokenKey = Base64.getEncoder().encodeToString(REFRESH_TOKEN_KEY.getBytes()).getBytes();
        this.refreshTokenKey = Keys.hmacShaKeyFor(encodedRefreshTokenKey);
    }

    public String createAccessToken(Authentication authentication) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();

        String username = userDetails.getUsername();
        String role = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(accessTokenKey)
                .compact();
    }

    public void createAndSaveRefreshToken(Authentication authentication, HttpServletResponse response) {
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(refreshTokenKey)
                .compact();

        PrincipalDetails userDetails = (PrincipalDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        userRepository.updateRefreshTokenByUsername(username, refreshToken);

        CookieUtil.addSameSiteCookie(response, "refresh_token", refreshToken, (int) (REFRESH_TOKEN_EXPIRE_TIME / 1000));
    }

    // Access Token 을 검사하고 얻은 정보로 Authentication 객체 생성
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        User find = userRepository.findByUsername(claims.getSubject()).orElseThrow(() -> new UsernameNotFoundException("username is not found"));

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        PrincipalDetails principal = new PrincipalDetails(find);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(accessTokenKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalStateException e) {
            log.info("JWT 토큰이 잘못되었습니다");
        }
        return false;
    }

    // Access Token 만료시 갱신때 사용할 정보를 얻기 위해 Claim 리턴
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessTokenKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}
