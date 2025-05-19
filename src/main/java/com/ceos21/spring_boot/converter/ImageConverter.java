package com.ceos21.spring_boot.converter;

import com.ceos21.spring_boot.domain.entity.Image;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ImageConverter {
    public static List<Image> toImage(List<String> imageUrls) {

        List<Image> Images = imageUrls.stream()
                .map(url -> Image.builder()
                        .imageUrl(url)
                        .build())
                .collect(Collectors.toList());
        return Images;
    }
}
