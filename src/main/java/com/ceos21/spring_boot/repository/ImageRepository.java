package com.ceos21.spring_boot.repository;

import com.ceos21.spring_boot.domain.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {

}

