package com.ceos21.spring_boot.controller;

import com.ceos21.spring_boot.base.ApiResponse;
import com.ceos21.spring_boot.base.auth.CustomUserDetails;
import com.ceos21.spring_boot.base.status.SuccessStatus;
import com.ceos21.spring_boot.domain.entity.Comment;
import com.ceos21.spring_boot.dto.Answer.AnswerRequestDTO;
import com.ceos21.spring_boot.dto.Answer.AnswerResponseDTO;
import com.ceos21.spring_boot.dto.comment.CommentRequestDTO;
import com.ceos21.spring_boot.dto.comment.CommentResponseDTO;
import com.ceos21.spring_boot.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/comment")
// 답변에 대한 댓글 + 질문에 대한 댓글
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성
    @Operation(summary="댓글 작성")
    @PostMapping(value="")
    public ApiResponse<CommentResponseDTO> addComment(@AuthenticationPrincipal CustomUserDetails userDetails, CommentRequestDTO request) {

        Long writerId = userDetails.getUserId();

        // 1. 댓글 작성 처리
        CommentResponseDTO result = commentService.addComment(writerId,request);

        // 2.성공 응답 + 업로드 결과 DTO 반환
        return ApiResponse.of(SuccessStatus._OK, result);

    }

    //댓글 삭제
    @Operation(summary="댓글 삭제")
    @DeleteMapping(value="")
    public ApiResponse<Void> deleteComment(@AuthenticationPrincipal CustomUserDetails userDetails,@RequestParam Long commentId) {

        Long userId = userDetails.getUserId();

        commentService.deleteComment(userId,commentId);;
        return ApiResponse.of(SuccessStatus._OK, null);

    }
}
