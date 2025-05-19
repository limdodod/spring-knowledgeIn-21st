package com.ceos21.spring_boot.service.Impl;

import com.ceos21.spring_boot.base.exception.CustomException;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import com.ceos21.spring_boot.converter.AnswerConverter;
import com.ceos21.spring_boot.domain.entity.*;
import com.ceos21.spring_boot.dto.Answer.*;
import com.ceos21.spring_boot.repository.AnswerRepository;
import com.ceos21.spring_boot.domain.enums.LikeStatus;
import com.ceos21.spring_boot.repository.CommentRepository;
import com.ceos21.spring_boot.repository.PostRepository;
import com.ceos21.spring_boot.repository.UserRepository;
import com.ceos21.spring_boot.service.AnswerService;
import com.ceos21.spring_boot.service.ImageService;
import com.ceos21.spring_boot.service.S3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;
    private final ImageService imageService;
    private final CommentRepository commentRepository;

    // 질문글에 대한 답변 작성
    @Transactional
    public AnswerResponseDTO addAnswer(Long writerId,AnswerRequestDTO answerRequest) {
        // 1. 질문글 가져오기
        Post post = postRepository.findById(answerRequest.getPostId())
                .orElseThrow(() -> new CustomException(ErrorStatus.POST_NOT_FOUND));

        // 2. 자신이 작성한 질문글엔 답변 불가
        if (post.getPostWriter().getId().equals(writerId)) {
            throw new CustomException(ErrorStatus.CANNOT_ANSWER);
        }


        // 3. 답변 작성자 가져오기
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));


        // 4. S3에 사진 업로드 후 String 형태의 URL 반환
        List<String> imageUrls = s3UploadService.saveFile(answerRequest.getFiles());

        // 5. 답변 생성
        Answer answer = AnswerConverter.toAnswer(answerRequest, post, writer);

        // 6. 이미지 엔티티 생성
        List<Image> images = imageService.saveImages(imageUrls);

        // 7. 답변과 이미지 엔티티 매핑
        images.forEach(img -> answer.addImage(img));

        // 8. 답변 저장
        answerRepository.save(answer);

        // 9. DTO로 변환하여 반환
        return AnswerConverter.toAnswerResponseDTO(answer);

    }

    public AnswerAndPostDTO getAnswersByPostId(Long postId) {

        //1. Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorStatus.POST_NOT_FOUND));

        //2. Post의 이미지 가져오기
        List<String> postImageUrls = post.getImages().stream()
                .map(Image::getImageUrl)
                .collect(Collectors.toList());

        //3. 모든 Answer 조회
        List<Answer> answers = answerRepository.findAllByPostId(postId);

        // 4. Answer를 AnswerResponseDTO로 변환 (AnswerConverter 사용)
        List<AnswerResponseDTO> answerDTOs = answers.stream()
                .map(AnswerConverter::toAnswerResponseDTO)
                .collect(Collectors.toList());

        // 5. 최종 DTO 생성
        return new AnswerAndPostDTO(
                post.getPostWriter().getId(),
                post.getTitle(),
                post.getContent(),
                postImageUrls,
                answerDTOs
        );
    }

    // 답변 삭제
    @Transactional
    public void deleteAnswer(Long answerId,Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new CustomException(ErrorStatus.ANSWER_NOT_FOUND));

        //Answer 작성자 != 삭제 요청자
        if (!answer.getAnswerWriter().getId().equals(userId)) {
            throw new CustomException(ErrorStatus.CANNOT_DELETE_ANSWER);
        }

        // answer삭제시 comment는 그대로 둠 -> soft delete
        answer.setIsDeleted(true);
    }

}
