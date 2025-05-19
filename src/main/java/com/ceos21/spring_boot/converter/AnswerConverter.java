package com.ceos21.spring_boot.converter;

import com.ceos21.spring_boot.domain.entity.*;
import com.ceos21.spring_boot.dto.Answer.AnswerRequestDTO;
import com.ceos21.spring_boot.dto.Answer.AnswerResponseDTO;
import com.ceos21.spring_boot.dto.Answer.LikeRequestDTO;
import com.ceos21.spring_boot.dto.Answer.LikeResponseDTO;

import java.util.List;
import java.util.stream.Collectors;


public class AnswerConverter {

    public static Answer toAnswer(AnswerRequestDTO AnswerRequest, Post post, User answerWriter) {

        return Answer.builder()
                .post(post)
                .answerWriter(answerWriter)
                .content(AnswerRequest.getContent())
                .build();
    }

    public static AnswerResponseDTO toAnswerResponseDTO(Answer answer) {
        // Answer 엔티티를 AnswerResponseDTO로 변환
        return AnswerResponseDTO.builder()
                .answerId(answer.getId())
                .content(answer.getContent())
                .postId(answer.getPost().getId())
                .postWriterId(answer.getPost().getPostWriter().getId())
                .answerWriterId(answer.getAnswerWriter().getId())
                .imageUrls(answer.getImages().stream().map(Image::getImageUrl).collect(Collectors.toList()))
                .build();
    }

    public static LikeDislike toLikeDislike(LikeRequestDTO likeRequest, Answer answer, User user) {
        return LikeDislike.builder()
                .answer(answer)
                .user(user)
                .likestatus(likeRequest.getLikeStatus()) // LikeStatus (LIKE 또는 DISLIKE)
                .build();
    }

    public static LikeResponseDTO toLikeResponseDTO(LikeDislike likeDislike) {
        // likedislike 엔티티를 likeresponsedto로 변환
        return LikeResponseDTO.builder()
                .likeId(likeDislike.getId())
                .answerId(likeDislike.getAnswer().getId())
                .userId(likeDislike.getUser().getId())
                .likeStatus(likeDislike.getLikeStatus()) // 상태 반환
                .build();
    }

}
