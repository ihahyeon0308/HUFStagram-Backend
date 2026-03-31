package com.hufs.haigram.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hufs.haigram.backend.domain.ConversationParticipantEntity;

public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipantEntity, Long> {

    @EntityGraph(attributePaths = {"user"})
    List<ConversationParticipantEntity> findAllByConversation_Id(String conversationId);
}
