package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "message_receipts")
public class MessageReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="message_id", nullable=false)
    private Message message;

    @ManyToOne @JoinColumn(name="contact_id", nullable=false)
    private Contact contact;

    @Column(name="delivered_at")
    private Instant deliveredAt;

    @Column(name="read_at")
    private Instant readAt;

    @Column(name="failed_at")
    private Instant failedAt;

    @Column(name="failure_reason", length=300)
    private String failureReason;

    // Getters y setters
}

