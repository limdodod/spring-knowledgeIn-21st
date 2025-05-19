package com.ceos21.spring_boot.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDTO {

    private String title;
    private String content;
    private List<String> hashtags;
    List<MultipartFile> files;
}
