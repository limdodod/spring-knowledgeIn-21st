package com.ceos21.spring_boot.service.Impl;

import com.ceos21.spring_boot.base.auth.JwtTokenProvider;
import com.ceos21.spring_boot.base.exception.CustomException;
import com.ceos21.spring_boot.converter.UserConverter;
import com.ceos21.spring_boot.domain.entity.User;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import com.ceos21.spring_boot.dto.user.*;
import com.ceos21.spring_boot.repository.RefreshTokenRepository;
import com.ceos21.spring_boot.repository.UserRepository;
import com.ceos21.spring_boot.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    //회원가입
    @Transactional
    public UserResponseDTO signUp(UserSignupRequestDTO request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorStatus.DUPLICATED_EMAIL);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // User 객체 생성
        User user = UserConverter.toUser(request, encodedPassword);


        // User 저장
        userRepository.save(user);

        return UserConverter.toUserResponseDTO(user);

    }

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request, HttpServletResponse response) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorStatus.INVALID_PASSWORD);
        }

        // JWT 생성
        TokenDTO tokenDTO = jwtTokenProvider.createToken(user);

        // 쿠키에 리프레시 토큰 저장
        jwtTokenProvider.setRefreshTokenInCookies(response, tokenDTO.getRefreshToken());

        return UserConverter.toLoginResponseDTO(user, tokenDTO.getAccessToken(), tokenDTO.getRefreshToken());
    }

    // 로그아웃
    @Transactional
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }


}