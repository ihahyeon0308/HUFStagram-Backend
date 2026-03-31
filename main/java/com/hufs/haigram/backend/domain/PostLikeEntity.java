package com.hufs.haigram.backend.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "post_likes",
    indexes = {
        @Index(name = "idx_post_like_post_user", columnList = "post_id,user_id", unique = true),
        @Index(name = "idx_post_like_user", columnList = "user_id")
    }
)
public class PostLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private Instant createdAt;

    protected PostLikeEntity() {
    }

    public PostLikeEntity(PostEntity post, UserEntity user, Instant createdAt) {
        this.post = post;
        this.user = user;
        this.createdAt = createdAt;
    }

    public PostEntity getPost() {
        return post;
    }

    public UserEntity getUser() {
        return user;
    }
}
