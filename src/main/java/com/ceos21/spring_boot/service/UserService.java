package com.ceos21.spring_boot.service;

import com.ceos21.spring_boot.dto.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface UserService {
    UserResponseDTO signUp(UserSignupRequestDTO request);
    LoginResponseDTO login(LoginRequestDTO request, HttpServletResponse response);
    void logout(Long userId);
}
