package com.servicioiphone.chatbot.model;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="organization_id", nullable=false)
    private Organization organization;

    @Column(name="full_name", length=120)
    private String fullName;

    @Column(length=30, unique=true)
    private String phone;

    @Column(length=150)
    private String email;

    @Column(name="country_code", length=5)
    private String countryCode;

    @Column(length=30)
    private String source;

    @Column(name="created_at", updatable=false)
    private Instant createdAt;

    @Column(name="updated_at")
    private Instant updatedAt;

    // Getters y setters
}
