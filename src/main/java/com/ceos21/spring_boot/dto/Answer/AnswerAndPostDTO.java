package com.ceos21.spring_boot.dto.Answer;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerAndPostDTO {

    private Long postWriter;
    private String postTitle;
    private String postContent;
    private List<String> postImageUrls;
    private List<AnswerResponseDTO> answers;

}
