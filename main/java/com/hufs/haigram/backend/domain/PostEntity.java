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
@Table(name = "posts", indexes = @Index(name = "idx_posts_created_at", columnList = "createdAt"))
public class PostEntity {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @Column(length = 2200, nullable = false)
    private String caption;

    @Column(length = 120, nullable = false)
    private String location;

    @Column(nullable = false)
    private Instant createdAt;

    protected PostEntity() {
    }

    public PostEntity(String id, UserEntity author, String caption, String location, Instant createdAt) {
        this.id = id;
        this.author = author;
        this.caption = caption;
        this.location = location;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public UserEntity getAuthor() {
        return author;
    }

    public String getCaption() {
        return caption;
    }

    public String getLocation() {
        return location;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
