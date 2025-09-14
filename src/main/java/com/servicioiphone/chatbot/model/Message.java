package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;

    @ManyToOne @JoinColumn(name="conversation_id", nullable=false)
    private Conversation conversation;

    @ManyToOne @JoinColumn(name="channel_account_id", nullable=false)
    private ChannelAccount channelAccount;

    @Column(name="from_me", nullable=false)
    private boolean fromMe;

    @Column(name="provider_message_id", length=120)
    private String providerMessageId;

    @Column(name="message_type", length=20, nullable=false)
    private String messageType;

    @Column(columnDefinition="text")
    private String content;

    @Column(length=20, nullable=false)
    private String status = "PENDING";

    @Column(name="error_detail", columnDefinition="text")
    private String errorDetail;

    @Column(name="created_at", updatable=false)
    private Instant createdAt;

    @Column(name="updated_at")
    private Instant updatedAt;

    // Getters y setters
}
