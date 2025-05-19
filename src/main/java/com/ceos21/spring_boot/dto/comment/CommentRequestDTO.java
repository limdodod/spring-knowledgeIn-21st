package com.ceos21.spring_boot.dto.comment;

import com.ceos21.spring_boot.domain.enums.TargetStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentRequestDTO {
    private String content;
    private TargetStatus targetStatus;  // POST or ANSWER
    private Long targetId;

}
