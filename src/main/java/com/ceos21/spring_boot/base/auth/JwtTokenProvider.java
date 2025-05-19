package com.ceos21.spring_boot.base.auth;

import com.ceos21.spring_boot.base.exception.CustomException;
import com.ceos21.spring_boot.domain.entity.RefreshToken;
import com.ceos21.spring_boot.dto.user.TokenDTO;
import com.ceos21.spring_boot.repository.RefreshTokenRepository;
import com.ceos21.spring_boot.repository.UserRepository;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Component;
import com.ceos21.spring_boot.domain.entity.User;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key SECRET_KEY;
    private final long accessTokenExpirationMillis;
    private final long refreshTokenExpirationMillis;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtProperties jwtProperties;

    @Autowired
    public JwtTokenProvider(JwtProperties jwtProperties,
                            RefreshTokenRepository refreshTokenRepository,
                            UserRepository userRepository) {
        this.jwtProperties = jwtProperties;
        this.SECRET_KEY = new SecretKeySpec(Base64.getDecoder().decode(jwtProperties.getSecretKey()),
                SignatureAlgorithm.HS512.getJcaName());
        this.accessTokenExpirationMillis = jwtProperties.getAccessTokenExpirationMinutes() * 60 * 1000L;
        this.refreshTokenExpirationMillis = jwtProperties.getRefreshTokenExpirationDays() * 24 * 60 * 60 * 1000L;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    //엑세스 토큰 생성
    public String createAccessToken(User user) {
        Claims claims = Jwts.claims().setSubject(user.getEmail());
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());

        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenExpirationMillis))
                .signWith(SECRET_KEY)
                .compact();
    }

    //리프레시 토큰 생성
    public String createRefreshToken(User user) {
        Date now = new Date();
        Claims claims = Jwts.claims();
        claims.put("userId", String.valueOf(user.getId()));
        return Jwts.builder()
                .claim("userId", String.valueOf(user.getId()))
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenExpirationMillis))
                .signWith(SECRET_KEY)
                .compact();
    }

    // 리프레시 토큰 + 엑세스 토큰
    public TokenDTO createToken(User user) {
        String accessToken = createAccessToken(user);
        String refreshToken;

        // 디비에서 리프레시토큰 조회 (userId로)
        RefreshToken existingToken = refreshTokenRepository.findByUserId(user.getId()).orElse(null);

        if (existingToken != null) {
            try {
                // 리프레시 토큰이 유효한지 확인
                Jwts.parserBuilder()
                        .setSigningKey(SECRET_KEY)
                        .build()
                        .parseClaimsJws(existingToken.getRefreshToken());

                //유효하며ㄴ 재사용
                refreshToken = existingToken.getRefreshToken();
            } catch (ExpiredJwtException e) {
                // 만료된 경우 새로 발급
                refreshToken = createRefreshToken(user);
                existingToken.setRefreshToken(refreshToken);
                refreshTokenRepository.save(existingToken);
            }
        } else {
            refreshToken = createRefreshToken(user);
            refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken));
        }

        return new TokenDTO(accessToken, refreshToken);
    }

    public Key getSecretKey() {
        return SECRET_KEY;
    }

    // 엑세스 토큰 재발급
    public TokenDTO refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshTokenFromCookies(request);

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            String userIdStr = claims.get("userId", String.class);
            Long userId = Long.parseLong(userIdStr);

            RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.INVALID_REFRESH_TOKEN));

            if (!savedToken.getRefreshToken().equals(refreshToken)) {
                throw new CustomException(ErrorStatus.INVALID_REFRESH_TOKEN);
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

            // 새 토큰 생성
            TokenDTO newTokenDTO = createToken(user);

            // DB에 리프레시 토큰 업데이트
            savedToken.setRefreshToken(newTokenDTO.getRefreshToken());

            // 쿠키에 새로운 리프레시 토큰 저장
            setRefreshTokenInCookies(response, newTokenDTO.getRefreshToken());

            return newTokenDTO;

        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorStatus.REFRESH_TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }
    }


    //리프레시 토큰 쿠키에 저장하기
    public void setRefreshTokenInCookies(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);
    }

    // 쿠키에서 리프레시 토큰 찾기
    public String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
