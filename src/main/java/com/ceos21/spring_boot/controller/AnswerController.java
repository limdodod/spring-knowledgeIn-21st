package com.ceos21.spring_boot.controller;

import com.ceos21.spring_boot.base.ApiResponse;
import com.ceos21.spring_boot.base.auth.CustomUserDetails;
import com.ceos21.spring_boot.base.exception.CustomException;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import com.ceos21.spring_boot.base.status.SuccessStatus;
import com.ceos21.spring_boot.domain.entity.Answer;
import com.ceos21.spring_boot.domain.entity.Post;
import com.ceos21.spring_boot.domain.entity.Image;
import com.ceos21.spring_boot.dto.Answer.*;
import com.ceos21.spring_boot.domain.enums.LikeStatus;
import com.ceos21.spring_boot.dto.post.PostResponseDTO;
import com.ceos21.spring_boot.repository.PostRepository;
import com.ceos21.spring_boot.repository.AnswerRepository;
import com.ceos21.spring_boot.service.AnswerService;
import com.ceos21.spring_boot.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
// likeDislike controller을 따로 만들지 않고 answerController에 포함

public class AnswerController {

    private final AnswerService answerService;
    private final LikeService likeService;

    // 답변 작성
    @Operation(summary="답변 작성")
    @PostMapping(value="/answer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AnswerResponseDTO> addAnswer(@AuthenticationPrincipal CustomUserDetails userDetails,@ModelAttribute @Valid AnswerRequestDTO request) {

        Long writerId = userDetails.getUserId();

        // 1. 답변 업로드 처리
        AnswerResponseDTO result = answerService.addAnswer(writerId,request);

        // 2.성공 응답 + 업로드 결과 DTO 반환
        return ApiResponse.of(SuccessStatus._OK, result);

    }

    // 답변 삭제
    @Transactional
    @Operation(summary="답변 삭제")
    @DeleteMapping(value="/answer")
    public ApiResponse<Void> deleteAnswer(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam Long answerId) {

        Long userId = userDetails.getUserId();

        answerService.deleteAnswer(answerId,userId);
        return ApiResponse.of(SuccessStatus._OK, null);

    }



    // 특정 질문에 대한 답변들 조회
    @Operation(summary="답변 조회")
    @GetMapping(value="permit/answer/{postId}")
    public ApiResponse<AnswerAndPostDTO> getAnswersByPostId(@PathVariable Long postId) {

        AnswerAndPostDTO result = answerService.getAnswersByPostId(postId);

        return ApiResponse.of(SuccessStatus._OK, result);

    }


    // 답변에 대한 좋아요/싫어요
    @Transactional
    @Operation(summary="좋아요/싫어요")
    @PostMapping(value="answer/likeDislike")
    ApiResponse<LikeResponseDTO> AddLikes(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody LikeRequestDTO likeRequestDTO) {

        Long userId = userDetails.getUserId();

        LikeResponseDTO likeResponse= likeService.addLikes(likeRequestDTO,userId);
        return ApiResponse.of(SuccessStatus._OK, likeResponse);

    }


    // 답변에 대한 좋아요/싫어요 삭제
    @Transactional
    @Operation(summary="좋아요/싫어요")
    @DeleteMapping(value="answer/likeDislike")
    public ApiResponse<Void> deleteLikes(@RequestParam Long answerId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();

        likeService.deleteLikes(answerId,userId);
        return ApiResponse.of(SuccessStatus._OK, null);

    }


}
