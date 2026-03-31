package com.hufs.haigram.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hufs.haigram.backend.domain.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, String> {

    @EntityGraph(attributePaths = {"author"})
    List<PostEntity> findAllByOrderByCreatedAtDesc();

    long countByAuthor_Id(String authorId);
}
