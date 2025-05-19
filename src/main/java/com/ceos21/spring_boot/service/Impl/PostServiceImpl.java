package com.ceos21.spring_boot.service.Impl;

import com.ceos21.spring_boot.base.exception.CustomException;
import com.ceos21.spring_boot.base.status.ErrorStatus;
import com.ceos21.spring_boot.converter.AnswerConverter;
import com.ceos21.spring_boot.converter.PostConverter;
import com.ceos21.spring_boot.domain.entity.*;
import com.ceos21.spring_boot.dto.Answer.AnswerResponseDTO;
import com.ceos21.spring_boot.dto.post.PostRequestDTO;
import com.ceos21.spring_boot.repository.*;
import com.ceos21.spring_boot.service.ImageService;
import com.ceos21.spring_boot.service.S3UploadService;
import com.ceos21.spring_boot.dto.post.PostResponseDTO;
import com.ceos21.spring_boot.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3UploadService s3UploadService;
    private final ImageService imageService;
    private final HashtagRepository hashtagRepository;
    private final AnswerRepository answerRepository;
    private final LikeDislikeRepository likeDislikeRepository;

    // 질문글에 대한 답변 작성
    @Transactional
    public PostResponseDTO addPost(PostRequestDTO postRequest,Long writerId) {

        // 1. 질문 작성자 가져오기
        User writer = userRepository.findById(writerId)
                .orElseThrow(() -> new CustomException(ErrorStatus.USER_NOT_FOUND));


        // 2. S3에 사진 업로드 후 String 형태의 URL 반환
        List<String> imageUrls = s3UploadService.saveFile(postRequest.getFiles());

        // 3. 질문 생성
        Post post = PostConverter.toPost(postRequest, writer);

        // 4. 이미지 엔티티 생성
        List<Image> images = imageService.saveImages(imageUrls);

        // 5. 질문과 이미지 매핑
        images.forEach(img -> post.addImage(img));

        // 6. 해시태그 처리
        List<PostHash> postHashtags = AddHashtags(postRequest.getHashtags(), post);
        post.setPostHashtags(postHashtags);

        // 7. 답변 저장
        postRepository.save(post);

        // 8. DTO로 변환하여 반환
        return PostConverter.toPostResponseDTO(post);
    }


    //내 질문글 가져오기
    public List<PostResponseDTO> getPostsByUserId(Long userId) {
        List<Post> posts = postRepository.findByPostWriterId(userId);

        return posts.stream()
                .map(PostConverter::toPostResponseDTO)
                .collect(Collectors.toList());
    }

    //질문글 삭제하기
    public void deletePost(Long postId, Long userId) {

        // 1. Post 조회
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorStatus.POST_NOT_FOUND));

        // 2. Post 작성자 = 요창자 ?
        if (!post.getPostWriter().getId().equals(userId)) {
            throw new CustomException(ErrorStatus.CANNOT_DELETE_POST);
        }

        // 3. Post에 속한 Answer 찾기
        List<Answer> answers = answerRepository.findByPostId(postId);
            //각 Answer에 속한 LikeDislike 삭제
        for (Answer answer : answers) {
            likeDislikeRepository.deleteByAnswerId(answer.getId());
        }

        //4. Post 삭제시 hashtag는 그대로
        List<PostHash> postHashtags = post.getPostHashtags();
        for (PostHash postHash : postHashtags) {
            postHash.setPost(null);
        }

        // 5. Post 삭제
        postRepository.delete(post);
    }

    // 해시태그 추가
    private List<PostHash> AddHashtags(List<String> hashtagContents, Post post) {
        List<PostHash> postHashtags = new ArrayList<>();

        for (String content : hashtagContents) {
            // 1. 기존 해시태그가 있는지 확인, 없으면 새로 저장
            Hashtag hashtag = hashtagRepository.findByContent(content)
                    .orElseGet(() -> hashtagRepository.save(new Hashtag(content))); // 없으면 새로 저장

            // 2. PostHash 객체 생성 후 리스트에 추가
            postHashtags.add(new PostHash(post, hashtag));
        }

        return postHashtags;
    }


    //해시태그 기준 글 검색
    public List<PostResponseDTO> getPostsByHashtag(String hashtag) {
        List<Post> posts = postRepository.findByHashtag(hashtag);

        return posts.stream()
                .map(PostConverter::toPostResponseDTO)
                .collect(Collectors.toList());
    }
}

