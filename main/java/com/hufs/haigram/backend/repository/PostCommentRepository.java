package com.hufs.haigram.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hufs.haigram.backend.domain.PostCommentEntity;

public interface PostCommentRepository extends JpaRepository<PostCommentEntity, String> {

    @EntityGraph(attributePaths = {"author"})
    List<PostCommentEntity> findAllByPost_IdOrderByCreatedAtAsc(String postId);
}
