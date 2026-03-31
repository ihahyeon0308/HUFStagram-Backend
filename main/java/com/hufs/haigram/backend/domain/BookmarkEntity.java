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
    name = "bookmarks",
    indexes = {
        @Index(name = "idx_bookmark_user_post", columnList = "user_id,post_id", unique = true),
        @Index(name = "idx_bookmark_user", columnList = "user_id")
    }
)
public class BookmarkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @Column(nullable = false)
    private Instant createdAt;

    protected BookmarkEntity() {
    }

    public BookmarkEntity(UserEntity user, PostEntity post, Instant createdAt) {
        this.user = user;
        this.post = post;
        this.createdAt = createdAt;
    }

    public UserEntity getUser() {
        return user;
    }

    public PostEntity getPost() {
        return post;
    }
}
