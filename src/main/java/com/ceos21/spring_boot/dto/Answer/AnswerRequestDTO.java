package com.ceos21.spring_boot.dto.Answer;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequestDTO {

    @NotNull(message = "post ID must not be null")
    private Long postId;
    private String content;

    List<MultipartFile> files;
}
