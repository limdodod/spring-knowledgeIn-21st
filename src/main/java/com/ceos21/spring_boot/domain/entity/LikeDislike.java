package com.ceos21.spring_boot.domain.entity;

import com.ceos21.spring_boot.domain.enums.LikeStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LikeDislike {
    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
            @Column(name="like_dislike_id")
            private Long id;

            @Enumerated(EnumType.STRING)
            private LikeStatus likestatus;

            @ManyToOne(fetch= FetchType.LAZY)
            @JoinColumn(name="user_id")
            private User user;

            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "answer_id", nullable = true)
            private Answer answer;

        public LikeStatus getLikeStatus() {
                return this.likestatus;
        }


}
