package com.ceos21.spring_boot.converter;

import com.ceos21.spring_boot.domain.entity.Answer;
import com.ceos21.spring_boot.domain.entity.Post;
import com.ceos21.spring_boot.domain.entity.User;
import com.ceos21.spring_boot.domain.enums.Role;
import com.ceos21.spring_boot.dto.Answer.AnswerRequestDTO;
import com.ceos21.spring_boot.dto.user.LoginResponseDTO;
import com.ceos21.spring_boot.dto.user.UserResponseDTO;
import com.ceos21.spring_boot.dto.user.UserSignupRequestDTO;

public class UserConverter {
    public static User toUser(UserSignupRequestDTO Request, String password ) {

        return  User.builder()
                .email(Request.getEmail())
                .password(password)
                .nickname(Request.getNickname())
                .role(Role.USER)
                .build();
    }

    public static UserResponseDTO toUserResponseDTO(User user ) {

        return UserResponseDTO.builder()
                .id (user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .build();
    }

    public static LoginResponseDTO toLoginResponseDTO(User user, String accessToken,String refreshToken) {
        return  LoginResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
