package com.ceos21.spring_boot.dto.Answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerResponseDTO {

    private Long answerId;
    private Long postWriterId;
    private Long postId;
    private Long answerWriterId;
    private String content;
    private List<String> imageUrls;

}
