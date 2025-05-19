package com.ceos21.spring_boot.service;

import com.ceos21.spring_boot.dto.Answer.LikeRequestDTO;
import com.ceos21.spring_boot.dto.Answer.LikeResponseDTO;

public interface LikeService {
    LikeResponseDTO addLikes(LikeRequestDTO likeRequest,Long userId);

    void deleteLikes(Long answeId,Long userId);
}
