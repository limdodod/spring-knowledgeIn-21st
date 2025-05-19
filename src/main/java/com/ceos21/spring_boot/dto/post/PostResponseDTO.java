package com.ceos21.spring_boot.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {

    private Long postId;
    private Long writerId;
    private String title;
    private String content;
    private List<String> hashtags;
    private List<String> imageUrls;

}
