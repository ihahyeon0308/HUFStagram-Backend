package com.hufs.haigram.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "conversation_participants",
    indexes = {
        @Index(name = "idx_conversation_participant_unique", columnList = "conversation_id,user_id", unique = true),
        @Index(name = "idx_conversation_participant_user", columnList = "user_id")
    }
)
public class ConversationParticipantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    protected ConversationParticipantEntity() {
    }

    public ConversationParticipantEntity(ConversationEntity conversation, UserEntity user) {
        this.conversation = conversation;
        this.user = user;
    }

    public ConversationEntity getConversation() {
        return conversation;
    }

    public UserEntity getUser() {
        return user;
    }
}
