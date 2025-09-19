package com.servicioiphone.chatbot.model;
// domain/Contact.java

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity @Table(name="contact")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contact {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, unique=true, length=32)
  private String waId;              // número WhatsApp en formato internacional, ej: 51920461960

  @Column(length=120)
  private String name;

  @CreationTimestamp
  private Instant createdAt;
  @UpdateTimestamp
  private Instant updatedAt;
}

