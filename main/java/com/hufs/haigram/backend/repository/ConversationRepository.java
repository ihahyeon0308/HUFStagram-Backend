package com.hufs.haigram.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hufs.haigram.backend.domain.ConversationEntity;

public interface ConversationRepository extends JpaRepository<ConversationEntity, String> {

    @Query("""
        select distinct c
        from ConversationEntity c
        join ConversationParticipantEntity p on p.conversation = c
        where p.user.id = :userId
        order by c.updatedAt desc
        """)
    List<ConversationEntity> findAllByParticipantUserIdOrderByUpdatedAtDesc(String userId);

    @Query("""
        select distinct c
        from ConversationEntity c
        join ConversationParticipantEntity p1 on p1.conversation = c
        join ConversationParticipantEntity p2 on p2.conversation = c
        where p1.user.id = :firstUserId and p2.user.id = :secondUserId
        """)
    List<ConversationEntity> findSharedConversation(String firstUserId, String secondUserId);
}
