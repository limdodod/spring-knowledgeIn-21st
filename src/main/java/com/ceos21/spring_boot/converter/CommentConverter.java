package com.ceos21.spring_boot.converter;

import com.ceos21.spring_boot.domain.entity.Answer;
import com.ceos21.spring_boot.domain.entity.Comment;
import com.ceos21.spring_boot.domain.entity.Post;
import com.ceos21.spring_boot.domain.entity.User;
import com.ceos21.spring_boot.dto.comment.CommentRequestDTO;
import com.ceos21.spring_boot.dto.comment.CommentResponseDTO;

public class CommentConverter {
    public static Comment toComment(CommentRequestDTO commentRequest, Post post,Answer answer, User commentWriter) {

        return Comment.builder()
                .targetStatus(commentRequest.getTargetStatus())
                .commentWriter(commentWriter)
                .content(commentRequest.getContent())
                .post(post)
                .answer(answer)
                .build();
    }

    public static CommentResponseDTO toCommentResponseDTO(Comment comment) {

        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .targetStatus(comment.getTargetStatus())
                .commentWriterId(comment.getCommentWriter().getId())
                .content(comment.getContent())
                .postId(comment.getPost() != null ? comment.getPost().getId() : null)
                .answerId(comment.getAnswer() != null ? comment.getAnswer().getId() : null)
                .build();
    }
}
