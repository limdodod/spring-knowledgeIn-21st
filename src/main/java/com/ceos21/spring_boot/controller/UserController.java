package com.ceos21.spring_boot.controller;

import com.ceos21.spring_boot.base.ApiResponse;
import com.ceos21.spring_boot.base.auth.CustomUserDetails;
import com.ceos21.spring_boot.base.auth.JwtTokenProvider;
import com.ceos21.spring_boot.base.exception.CustomException;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import com.ceos21.spring_boot.base.status.SuccessStatus;
import com.ceos21.spring_boot.domain.entity.User;
import com.ceos21.spring_boot.dto.user.*;
import com.ceos21.spring_boot.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입
    @Operation(summary="회원가입")
    @PostMapping("/create")
    public ApiResponse<UserResponseDTO> signUp(@RequestBody @Valid UserSignupRequestDTO request) {

        // 성공 응답 + 회원가입 결과 DTO 반환
        UserResponseDTO result = userService.signUp(request);
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    // 로그인
    @Operation(summary= "로그인")
    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {

        // 사용자 이메일로 사용자 조회 + 리프레시 토큰을 쿠키에 저장
        LoginResponseDTO result = userService.login(request, response);

        // 성공 응답 반환
        return ApiResponse.of(SuccessStatus._OK, result);
    }

    @Operation(summary = "엑세스 토큰 재발급")
    @PostMapping("/refresh")
    public ApiResponse<TokenDTO> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = jwtTokenProvider.getRefreshTokenFromCookies(request);

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new CustomException(ErrorStatus.INVALID_REFRESH_TOKEN);
        }

        // 리프레시 토큰으로 새로운 엑세스 토큰 재발급
        TokenDTO newTokens = jwtTokenProvider.refreshAccessToken(request,response);

        // 성공 응답 반환
        return ApiResponse.of(SuccessStatus._OK, newTokens);
    }

    // 로그아웃
    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        userService.logout(userId);

        return ApiResponse.of(SuccessStatus._OK,null);
    }
}