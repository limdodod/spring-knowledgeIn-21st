package com.ceos21.spring_boot.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="post_id")
    private Long id;

    @Column(length=30)
    private String title;

    private String content;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Image> images;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Answer> answers;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
    private List<PostHash> postHashtags;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="writer_id")
    private User postWriter;

    public void addImage(Image image) {
        if (this.images == null) {
            this.images = new ArrayList<>();
        }
        this.images.add(image);
        image.setPost(this);
    }

    public void setPostHashtags(List<PostHash> postHashtags) {
        this.postHashtags = postHashtags;
    }

}
