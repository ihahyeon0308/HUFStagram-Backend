package com.hufs.haigram.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hufs.haigram.backend.domain.BookmarkEntity;

public interface BookmarkRepository extends JpaRepository<BookmarkEntity, Long> {

    Optional<BookmarkEntity> findByUser_IdAndPost_Id(String userId, String postId);

    List<BookmarkEntity> findAllByUser_Id(String userId);
}
