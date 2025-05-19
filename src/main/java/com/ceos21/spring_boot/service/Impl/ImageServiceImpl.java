package com.ceos21.spring_boot.service.Impl;

import com.ceos21.spring_boot.converter.ImageConverter;
import com.ceos21.spring_boot.domain.entity.Image;
import com.ceos21.spring_boot.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

        public List<Image> saveImages(List<String> imageUrls) {
            return ImageConverter.toImage(imageUrls); // 이미지 엔티티로 변환
        }
    }
