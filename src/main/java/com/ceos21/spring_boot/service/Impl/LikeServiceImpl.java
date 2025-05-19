package com.ceos21.spring_boot.service.Impl;

import com.ceos21.spring_boot.base.exception.CustomException;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import com.ceos21.spring_boot.converter.AnswerConverter;
import com.ceos21.spring_boot.domain.entity.Answer;
import com.ceos21.spring_boot.domain.entity.LikeDislike;
import com.ceos21.spring_boot.domain.entity.User;
import com.ceos21.spring_boot.domain.enums.LikeStatus;
import com.ceos21.spring_boot.dto.Answer.LikeRequestDTO;
import com.ceos21.spring_boot.dto.Answer.LikeResponseDTO;
import com.ceos21.spring_boot.repository.AnswerRepository;
import com.ceos21.spring_boot.repository.LikeDislikeRepository;
import com.ceos21.spring_boot.repository.UserRepository;
import com.ceos21.spring_boot.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;
    private final LikeDislikeRepository likeDislikeRepository;

    // 좋아요/싫어요 달기
    @Transactional
    public LikeResponseDTO addLikes(LikeRequestDTO likeRequest,Long userId) {

        //1. 답변 조회
        Answer answer = answerRepository.findById(likeRequest.getAnswerId())
                .orElseThrow(() -> new CustomException(ErrorStatus.ANSWER_NOT_FOUND));

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 기존 likeDislike 존재 확인
        LikeDislike existingLikeDislike = likeDislikeRepository.findByAnswerAndUser(answer, user)
                .orElse(null);

        if (existingLikeDislike != null) {
            // 3.1. 기존 상태가 좋아요(LIKE)일 경우
            if (existingLikeDislike.getLikeStatus() == LikeStatus.LIKE) {

                if (likeRequest.getLikeStatus() == LikeStatus.DISLIKE) {
                    // 좋아요 상태에서 싫어요 누름
                    throw new CustomException(ErrorStatus.CANNOT_CHECK_BOTH);
                }
                    // 좋아요 상태에서 좋아요 또 누름
                else if (likeRequest.getLikeStatus() == LikeStatus.LIKE) {
                    throw new CustomException(ErrorStatus.DUPLICATE_LIKE);
                }
                return AnswerConverter.toLikeResponseDTO(null);
            }
            // 3.2. 기존 상태가 싫어요(DISLIKE)일 경우
            else if (existingLikeDislike.getLikeStatus() == LikeStatus.DISLIKE) {

                if (likeRequest.getLikeStatus() == LikeStatus.LIKE) {
                    // 싫어요 상태에서 좋아요 누름
                    throw new CustomException(ErrorStatus.CANNOT_CHECK_BOTH);
                }
                    // 싫어요 상태에서 싫어요 또 누름
                else if (likeRequest.getLikeStatus() == LikeStatus.DISLIKE) {
                    throw new CustomException(ErrorStatus.DUPLICATE_DISLIKE);
                }
                    return AnswerConverter.toLikeResponseDTO(null);
                }
            }


        // 3. LikeDislike 객체 생성
        LikeDislike likeDislike = AnswerConverter.toLikeDislike(likeRequest, answer, user);


        // 4. LikeDislike 저장
        LikeDislike savedLikeDislike = likeDislikeRepository.save(likeDislike);

        // 5. 응답 DTO 생성
        return AnswerConverter.toLikeResponseDTO(savedLikeDislike);
    }

    // 좋아요/싫어요 삭제
    @Transactional
    public void deleteLikes(Long answerId, Long userId) {

        // 1. 답변 조회
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new CustomException(ErrorStatus.ANSWER_NOT_FOUND));

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));

        // 3. 좋아요/싫어요 조회
        LikeDislike likeDislike = likeDislikeRepository.findByAnswerAndUser(answer, user)
                .orElseThrow(() -> new CustomException(ErrorStatus.LIKE_DISLIKE_NOT_FOUND));

        // 4. 좋아요 싫어요 작성자 = 요창자 ?
        if (!likeDislike.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorStatus.CANNOT_DELETE_LIKES);
        }

        // 5. 좋아요/싫어요 삭제
        likeDislikeRepository.delete(likeDislike);
    }

}