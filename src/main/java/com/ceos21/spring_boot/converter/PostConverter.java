package com.ceos21.spring_boot.converter;

import com.ceos21.spring_boot.domain.entity.*;
import com.ceos21.spring_boot.dto.Answer.LikeRequestDTO;
import com.ceos21.spring_boot.dto.Answer.LikeResponseDTO;
import com.ceos21.spring_boot.dto.post.PostRequestDTO;
import com.ceos21.spring_boot.dto.post.PostResponseDTO;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

public class PostConverter {

    public static Post toPost(PostRequestDTO PostRequest, User writer) {

    return Post.builder()
            .postWriter(writer)
            .title(PostRequest.getTitle())
            .content(PostRequest.getContent())
            .build();}

    public static PostResponseDTO toPostResponseDTO(Post post) {

    // Post 엔티티를 PostResponseDTO로 변환
    return PostResponseDTO.builder()
            .postId(post.getId())
            .title(post.getTitle())
            .content(post.getContent())
            .writerId(post.getPostWriter().getId())
            .hashtags(post.getPostHashtags() != null && !post.getPostHashtags().isEmpty() ?
                    post.getPostHashtags().stream()
                            .map(postHash -> postHash.getHashtag().getContent()) // Hashtag의 content 반환
                            .collect(Collectors.toList())
                    : new ArrayList<>()) // null이거나 빈 리스트인 경우 빈 리스트 반환
            .imageUrls(post.getImages().stream()
                    .map(Image::getImageUrl)
                    .collect(Collectors.toList()))
            .build();
    }

}

