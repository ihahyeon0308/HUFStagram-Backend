package com.hufs.haigram.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hufs.haigram.backend.domain.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, String> {

    @EntityGraph(attributePaths = {"sender"})
    List<MessageEntity> findAllByConversation_IdOrderByCreatedAtAsc(String conversationId);
}
