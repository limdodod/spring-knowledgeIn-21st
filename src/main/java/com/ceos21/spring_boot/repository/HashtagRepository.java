package com.ceos21.spring_boot.repository;

import com.ceos21.spring_boot.domain.entity.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashtagRepository extends JpaRepository<Hashtag, Long> {

    Optional<Hashtag> findByContent(String content);
}
