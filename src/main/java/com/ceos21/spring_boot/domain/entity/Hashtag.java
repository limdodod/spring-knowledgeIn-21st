package com.ceos21.spring_boot.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hashtag extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="hashtag_id")
    private Long id;

    @Column(length=30)
    private String content;

    @OneToMany(mappedBy = "hashtag")
    private List<PostHash> postHashtags;

    // String만 받는 생성자 추가
    public Hashtag(String content) {
        this.content = content;
    }

}
