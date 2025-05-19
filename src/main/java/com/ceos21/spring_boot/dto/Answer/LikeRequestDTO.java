package com.ceos21.spring_boot.dto.Answer;

import com.ceos21.spring_boot.domain.enums.LikeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LikeRequestDTO {
    private Long answerId;
    private LikeStatus likeStatus;
}
