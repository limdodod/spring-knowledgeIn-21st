package com.ceos21.spring_boot.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {
    private Long id;
    private String email;
    private String nickname;
    private String role;
    private String accessToken;
    private String refreshToken;
}