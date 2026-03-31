package com.hufs.haigram.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hufs.haigram.backend.domain.PostMediaEntity;

public interface PostMediaRepository extends JpaRepository<PostMediaEntity, String> {

    List<PostMediaEntity> findAllByPost_IdOrderByOrderIndexAsc(String postId);
}
