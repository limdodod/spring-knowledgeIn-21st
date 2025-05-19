package com.ceos21.spring_boot.repository;

import com.ceos21.spring_boot.domain.entity.Answer;
import com.ceos21.spring_boot.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByPostId(Long postId);
    List<Answer> findAllByPostId(Long postId);
}
