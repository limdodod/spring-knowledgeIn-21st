package com.ceos21.spring_boot.repository;

import com.ceos21.spring_boot.domain.entity.Answer;
import com.ceos21.spring_boot.domain.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAnswer(Answer answer);
}
