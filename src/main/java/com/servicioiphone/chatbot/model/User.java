package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;

    @Column(nullable=false, length=120)
    private String name;

    @Column(nullable=false, length=150, unique=true)
    private String email;

    @Column(nullable=false, length=40)
    private String role;

    @Column(name="is_active", nullable=false)
    private boolean active = true;

    @Column(name="last_login_at")
    private Instant lastLoginAt;

    @Column(name="created_at", updatable=false)
    private Instant createdAt;

    @Column(name="updated_at")
    private Instant updatedAt;

    // Getters y setters
}

