package com.ceos21.spring_boot.domain.entity;

import com.ceos21.spring_boot.domain.enums.TargetStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="writer_id")
    private User commentWriter;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="post_id")
    private Post post;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="answer_id",nullable = true)
    private Answer answer;


    @Enumerated(EnumType.STRING)
    private TargetStatus targetStatus;

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }


}
