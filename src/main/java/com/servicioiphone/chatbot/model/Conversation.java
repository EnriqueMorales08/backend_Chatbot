package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;

@Entity @Table(name="conversation",
  indexes = {@Index(name="ix_conv_contact", columnList = "contact_id", unique = true)})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Conversation {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional=false, fetch=FetchType.LAZY)
  private Contact contact;

  @CreationTimestamp
  private Instant createdAt;

  private Instant lastMessageAt;
}
