package com.ceos21.spring_boot.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Image extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="answer_id")
    private Answer answer;


    public void setPost(Post post) {
        this.post = post;
    }

    //연관관계 편의 메서드
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}