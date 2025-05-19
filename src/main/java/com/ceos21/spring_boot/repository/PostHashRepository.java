package com.ceos21.spring_boot.repository;

import com.ceos21.spring_boot.domain.entity.PostHash;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashRepository extends JpaRepository<PostHash, Long> {
}
