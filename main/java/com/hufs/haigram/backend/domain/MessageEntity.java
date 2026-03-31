package com.hufs.haigram.backend.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages", indexes = @Index(name = "idx_messages_conversation_created", columnList = "conversation_id,createdAt"))
public class MessageEntity {

    @Id
    @Column(length = 64, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private UserEntity sender;

    @Column(length = 500, nullable = false)
    private String text;

    @Column(nullable = false)
    private Instant createdAt;

    protected MessageEntity() {
    }

    public MessageEntity(String id, ConversationEntity conversation, UserEntity sender, String text, Instant createdAt) {
        this.id = id;
        this.conversation = conversation;
        this.sender = sender;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public ConversationEntity getConversation() {
        return conversation;
    }

    public UserEntity getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
