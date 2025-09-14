package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "webhook_events")
public class WebhookEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;

    @ManyToOne @JoinColumn(name="channel_account_id", nullable=false)
    private ChannelAccount channelAccount;

    @Column(name="event_type", length=50)
    private String eventType;

    @Column(columnDefinition="text")
    private String payload; // JSON

    @Column(name="received_at")
    private Instant receivedAt;

    @Column(name="processed_at")
    private Instant processedAt;

    @Column(length=500)
    private String error;

    // Getters y setters
}
