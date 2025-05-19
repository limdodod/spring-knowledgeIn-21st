package com.ceos21.spring_boot.dto.comment;

import com.ceos21.spring_boot.domain.enums.TargetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long commentId;
    private Long commentWriterId;
    private TargetStatus targetStatus;
    private Long postId;
    private Long answerId;
    private String content;

}
