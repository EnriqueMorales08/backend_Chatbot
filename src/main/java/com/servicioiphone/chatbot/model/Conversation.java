package com.servicioiphone.chatbot.model;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "conversations")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;

    @ManyToOne @JoinColumn(name="contact_id", nullable=false)
    private Contact contact;

    @ManyToOne @JoinColumn(name="channel_account_id", nullable=false)
    private ChannelAccount channelAccount;

    @ManyToOne @JoinColumn(name="assigned_user_id")
    private User assignedUser;

    @Column(nullable=false, length=20)
    private String status = "OPEN";

    @Column(length=120)
    private String subject;

    private Integer priority;
    private Integer score;

    @Column(name="first_message_at")
    private Instant firstMessageAt;

    @Column(name="last_message_at")
    private Instant lastMessageAt;

    @Column(name="created_at", updatable=false)
    private Instant createdAt;

    @Column(name="updated_at")
    private Instant updatedAt;

    // Getters y setters
}
