package com.hufs.haigram.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hufs.haigram.backend.domain.ActivityEntity;

public interface ActivityRepository extends JpaRepository<ActivityEntity, String> {

    List<ActivityEntity> findAllByUser_IdOrderByCreatedAtDesc(String userId);
}
