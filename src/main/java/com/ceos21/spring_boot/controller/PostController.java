package com.ceos21.spring_boot.controller;

import com.ceos21.spring_boot.base.ApiResponse;
import com.ceos21.spring_boot.base.auth.CustomUserDetails;
import com.ceos21.spring_boot.base.status.SuccessStatus;
import com.ceos21.spring_boot.dto.Answer.AnswerRequestDTO;
import com.ceos21.spring_boot.dto.Answer.AnswerResponseDTO;
import com.ceos21.spring_boot.dto.post.PostRequestDTO;
import com.ceos21.spring_boot.dto.post.PostResponseDTO;
import com.ceos21.spring_boot.service.AnswerService;
import com.ceos21.spring_boot.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final AnswerService answerService;

    // 질문 작성
    @Operation(summary="질문 작성")
    @PostMapping(value="/post", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponseDTO> addPost(@AuthenticationPrincipal CustomUserDetails userDetails,@ModelAttribute @Valid PostRequestDTO request) {

        Long writerId = userDetails.getUserId();
        // 1. 질문 업로드 처리
        PostResponseDTO result = postService.addPost(request,writerId);

        // 2.성공 응답 + 업로드 결과 DTO 반환
        return ApiResponse.of(SuccessStatus._OK, result);

    }

    // 내가 쓴 모든 질문 조회
    @Operation(summary = "내가 쓴 질문 조회")
    @GetMapping(value="/post")
    public ApiResponse<List<PostResponseDTO>> getMyPosts(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();

        // 1. 내가 쓴 질문 가져오기
        List<PostResponseDTO> myPosts = postService.getPostsByUserId(userId);

        // 2. 성공 응답 반환
        return ApiResponse.of(SuccessStatus._OK, myPosts);
    }

    @Transactional
    //내가 쓴 질문 삭제
    @Operation(summary = "내가 쓴 질문 삭제")
    @DeleteMapping(value="/post")
    public ApiResponse<Void> deletePost(@RequestParam Long postId, @AuthenticationPrincipal CustomUserDetails userDetails) {

        Long userId = userDetails.getUserId();
        postService.deletePost(postId, userId);
        return ApiResponse.of(SuccessStatus._OK, null);
    }

    // 해시태그별 글 조회
    @Operation(summary = "해시태그별 글 조회")
    @GetMapping(value="/permit/hashtag")
    public ApiResponse<List<PostResponseDTO>> getPostsByHashtag(@RequestParam String hashtag) {

        // 1. 내가 쓴 질문 가져오기
        List<PostResponseDTO> Posts = postService.getPostsByHashtag(hashtag);

        // 2. 성공 응답 반환
        return ApiResponse.of(SuccessStatus._OK, Posts);
    }


}
