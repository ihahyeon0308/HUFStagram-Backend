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
@Table(name = "activities", indexes = @Index(name = "idx_activities_user_created", columnList = "user_id,createdAt"))
public class ActivityEntity {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(length = 32, nullable = false)
    private String type;

    @Column(length = 255, nullable = false)
    private String text;

    @Column(nullable = false)
    private Instant createdAt;

    protected ActivityEntity() {
    }

    public ActivityEntity(String id, UserEntity user, String type, String text, Instant createdAt) {
        this.id = id;
        this.user = user;
        this.type = type;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
