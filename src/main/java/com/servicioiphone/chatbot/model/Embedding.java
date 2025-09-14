package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "embeddings")
public class Embedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;

    @Column(name="entity_type", length=20, nullable=false)
    private String entityType; // KB_CHUNK, MESSAGE, etc.

    @Column(name="entity_id", nullable=false)
    private Long entityId;

    @Column(length=60, nullable=false)
    private String model;

    @Column(nullable=false)
    private Integer dim;

    @Column(name="vector", columnDefinition="TEXT")
    private String vector; // representación como string

    // Getters y setters
}
