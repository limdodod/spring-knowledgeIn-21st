package com.ceos21.spring_boot.domain.entity;

import com.ceos21.spring_boot.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    private String password;

    @Column(length=20)
    private String nickname;

    @OneToMany(mappedBy = "commentWriter", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @OneToMany(mappedBy = "postWriter", cascade = CascadeType.ALL)
    private List<Post> posts;

    @OneToMany(mappedBy = "answerWriter", cascade = CascadeType.ALL)
    private List<Answer> answers;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
}
