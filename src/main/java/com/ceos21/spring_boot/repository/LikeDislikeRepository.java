package com.ceos21.spring_boot.repository;

import com.ceos21.spring_boot.domain.entity.Answer;
import com.ceos21.spring_boot.domain.entity.LikeDislike;
import com.ceos21.spring_boot.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeDislikeRepository extends JpaRepository<LikeDislike, Long> {

    Optional<LikeDislike> findByAnswerAndUser(Answer answer, User user);

    void deleteByAnswerId(Long answerId);
}
