package com.hufs.haigram.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hufs.haigram.backend.domain.FollowRelationEntity;

public interface FollowRelationRepository extends JpaRepository<FollowRelationEntity, Long> {

    long countByFollowing_Id(String followingId);

    List<FollowRelationEntity> findAllByFollower_Id(String followerId);

    Optional<FollowRelationEntity> findByFollower_IdAndFollowing_Id(String followerId, String followingId);
}
