package com.hufs.haigram.backend.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "post_comments", indexes = @Index(name = "idx_post_comments_post_created", columnList = "post_id,createdAt"))
public class PostCommentEntity {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @Column(length = 280, nullable = false)
    private String text;

    @Column(nullable = false)
    private Instant createdAt;

    protected PostCommentEntity() {
    }

    public PostCommentEntity(String id, PostEntity post, UserEntity author, String text, Instant createdAt) {
        this.id = id;
        this.post = post;
        this.author = author;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public PostEntity getPost() {
        return post;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
