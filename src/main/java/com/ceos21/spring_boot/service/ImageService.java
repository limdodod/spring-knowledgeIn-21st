package com.ceos21.spring_boot.service;

import com.ceos21.spring_boot.domain.entity.Image;

import java.util.List;

public interface ImageService {
    List<Image> saveImages(List<String> imageUrls);
}
