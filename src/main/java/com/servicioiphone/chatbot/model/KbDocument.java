package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "kb_documents")
public class KbDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;

    @Column(nullable=false, length=200)
    private String title;

    @Column(name="source_type", length=20, nullable=false)
    private String sourceType; // FILE, URL, MANUAL

    @Column(name="source_url", length=500)
    private String sourceUrl;

    @Column(columnDefinition="text")
    private String metadata; // JSON

    @Column(name="created_by_user_id")
    private Long createdByUserId;

    @Column(name="deleted_at")
    private Instant deletedAt;

    @Column(name="created_at", updatable=false)
    private Instant createdAt;

    @Column(name="updated_at")
    private Instant updatedAt;

    // Getters y setters
}
