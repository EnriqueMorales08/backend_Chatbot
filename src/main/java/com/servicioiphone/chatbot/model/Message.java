package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "chat_message", indexes = {
        @Index(name = "ix_msg_conversation", columnList = "conversation_id, createdAt")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 4)
    private Direction direction; // IN o OUT

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    private String waMessageId;   // ID de WhatsApp si lo mandas desde n8n
    private Instant waTimestamp;  // Timestamp del mensaje en WhatsApp

    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @CreationTimestamp
    private Instant createdAt;
}
