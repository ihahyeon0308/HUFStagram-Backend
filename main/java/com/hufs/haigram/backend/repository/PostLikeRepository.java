package com.hufs.haigram.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hufs.haigram.backend.domain.PostLikeEntity;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {

    Optional<PostLikeEntity> findByPost_IdAndUser_Id(String postId, String userId);

    List<PostLikeEntity> findAllByUser_Id(String userId);

    List<PostLikeEntity> findAllByPost_Id(String postId);
}
