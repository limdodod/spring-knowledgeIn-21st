package com.ceos21.spring_boot.service.Impl;

import com.ceos21.spring_boot.base.exception.CustomException;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import com.ceos21.spring_boot.converter.AnswerConverter;
import com.ceos21.spring_boot.converter.CommentConverter;
import com.ceos21.spring_boot.domain.entity.*;
import com.ceos21.spring_boot.domain.enums.TargetStatus;
import com.ceos21.spring_boot.dto.comment.CommentRequestDTO;
import com.ceos21.spring_boot.dto.Answer.AnswerResponseDTO;
import com.ceos21.spring_boot.dto.comment.CommentResponseDTO;
import com.ceos21.spring_boot.repository.AnswerRepository;
import com.ceos21.spring_boot.repository.CommentRepository;
import com.ceos21.spring_boot.repository.PostRepository;
import com.ceos21.spring_boot.repository.UserRepository;
import com.ceos21.spring_boot.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final PostRepository postRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 댓글 작성
    @Transactional
    public CommentResponseDTO addComment(Long writerId, CommentRequestDTO request) {

        User commentWriter = userRepository.findById(writerId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        //1. 댓글 어디에 달건지? targetStatus 가져오기
        Post post = null;
        Answer answer = null;

        if (request.getTargetStatus() == TargetStatus.POST) {
            post = postRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new CustomException(ErrorStatus.POST_NOT_FOUND));
        } else if (request.getTargetStatus() == TargetStatus.ANSWER) {
            answer = answerRepository.findById(request.getTargetId())
                    .orElseThrow(() -> new CustomException(ErrorStatus.ANSWER_NOT_FOUND));
        }

        Comment comment = CommentConverter.toComment(request, post, answer, commentWriter);

        commentRepository.save(comment);

        return CommentConverter.toCommentResponseDTO(comment);
    }

    //댓글 삭제
    @Transactional
    public void deleteComment(Long writerId,Long commentId) {
        System.out.println("Deleting comment with id: " + commentId);
        //1. 댓글 조회
        Comment comment= commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorStatus.COMMENT_NOT_FOUND));

        //2. 댓글 작성자 = 요창자 ?
        if (!comment.getCommentWriter().getId().equals(writerId)) {
            throw new CustomException(ErrorStatus.CANNOT_DELETE_COMMENT);
        }

        commentRepository.deleteById(commentId);
    }
}
