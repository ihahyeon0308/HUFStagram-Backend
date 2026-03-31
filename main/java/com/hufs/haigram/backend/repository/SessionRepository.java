package com.hufs.haigram.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hufs.haigram.backend.domain.SessionEntity;

public interface SessionRepository extends JpaRepository<SessionEntity, String> {
}
