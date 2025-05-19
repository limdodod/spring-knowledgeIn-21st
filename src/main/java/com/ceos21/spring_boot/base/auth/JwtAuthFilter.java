package com.ceos21.spring_boot.base.auth;

import com.ceos21.spring_boot.base.exception.CustomException;
import com.ceos21.spring_boot.dto.user.TokenDTO;
import com.ceos21.spring_boot.service.Impl.UserServiceImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = request.getHeader("Authorization");
        String refreshToken = request.getHeader("refreshToken");

        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7); // "Bearer " 제거
                Key key =  jwtTokenProvider.getSecretKey();

                // JWT 파싱
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();

                Long userId = Long.valueOf(claims.get("userId").toString());  // ★ userId를 claims에서 꺼내야 함
                String username = claims.getSubject();

                // 권한 목록 설정 (예시로 role만)
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));

                UserDetails userDetails = new CustomUserDetails(userId, username, null, authorities);

                // Authentication 객체 설정
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException | IllegalArgumentException e) {
            if (e instanceof ExpiredJwtException) {

                if (refreshToken != null) {
                    TokenDTO newTokens = jwtTokenProvider.refreshAccessToken(request, response);
                    response.setHeader("NewAccessToken", newTokens.getAccessToken());
                    response.setHeader("NewRefreshToken", newTokens.getRefreshToken());
                } else {
                    throw new CustomException(ErrorStatus.REFRESH_TOKEN_EXPIRED);
                }
            } else {
                throw new CustomException(ErrorStatus.COMMON_BAD_REQUEST);
            }
        }

        filterChain.doFilter(request, response);
    }
}
