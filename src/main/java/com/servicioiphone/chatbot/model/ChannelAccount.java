package com.servicioiphone.chatbot.model;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "channel_accounts")
public class ChannelAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;

    @Column(name="channel_code", length=20, nullable=false)
    private String channelCode; // WHATSAPP, TELEGRAM, etc.

    @Column(name="display_name", length=80)
    private String displayName;

    @Column(name="provider_metadata", columnDefinition="TEXT")
    private String providerMetadata; // JSON string

    @Column(name="created_at", updatable=false)
    private Instant createdAt;

    @Column(name="updated_at")
    private Instant updatedAt;

    // Getters y setters
}

