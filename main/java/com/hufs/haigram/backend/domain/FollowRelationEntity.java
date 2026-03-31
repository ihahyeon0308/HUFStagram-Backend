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
    name = "follow_relations",
    indexes = {
        @Index(name = "idx_follow_follower_following", columnList = "follower_id,following_id", unique = true),
        @Index(name = "idx_follow_following", columnList = "following_id")
    }
)
public class FollowRelationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private UserEntity follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    private UserEntity following;

    @Column(nullable = false)
    private Instant createdAt;

    protected FollowRelationEntity() {
    }

    public FollowRelationEntity(UserEntity follower, UserEntity following, Instant createdAt) {
        this.follower = follower;
        this.following = following;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getFollower() {
        return follower;
    }

    public UserEntity getFollowing() {
        return following;
    }
}
