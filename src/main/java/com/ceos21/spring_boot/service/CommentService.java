package com.ceos21.spring_boot.service;

import com.ceos21.spring_boot.dto.comment.CommentResponseDTO;
import com.ceos21.spring_boot.dto.comment.CommentRequestDTO;

public interface CommentService {
    CommentResponseDTO addComment(Long writerId, CommentRequestDTO commentRequest);
    void deleteComment(Long writerId,Long commentId);
}
