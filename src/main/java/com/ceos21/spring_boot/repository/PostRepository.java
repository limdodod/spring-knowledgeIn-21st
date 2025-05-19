package com.ceos21.spring_boot.repository;

import com.ceos21.spring_boot.domain.entity.Post;
import com.ceos21.spring_boot.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByPostWriterId(Long postWriterId);

    @Query("SELECT p FROM Post p JOIN p.postHashtags ph JOIN ph.hashtag h WHERE h.content = :hashtag")
    List<Post> findByHashtag(String hashtag);

}
