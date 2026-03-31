package com.hufs.haigram.backend.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_users_email", columnList = "email", unique = true),
        @Index(name = "idx_users_username", columnList = "username", unique = true)
    }
)
public class UserEntity {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @Column(length = 120, nullable = false, unique = true)
    private String email;

    @Column(length = 30, nullable = false, unique = true)
    private String username;

    @Column(length = 80, nullable = false)
    private String fullName;

    @Column(length = 160, nullable = false)
    private String bio;

    @Column(length = 12, nullable = false)
    private String initials;

    @Column(length = 120, nullable = false)
    private String accent;

    @Column(length = 120, nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private Instant createdAt;

    protected UserEntity() {
    }

    public UserEntity(
        String id,
        String email,
        String username,
        String fullName,
        String bio,
        String initials,
        String accent,
        String passwordHash,
        Instant createdAt
    ) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.fullName = fullName;
        this.bio = bio;
        this.initials = initials;
        this.accent = accent;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getInitials() {
        return initials;
    }

    public String getAccent() {
        return accent;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
