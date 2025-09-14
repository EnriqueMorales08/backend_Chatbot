package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "conversation_ai_steps")
public class ConversationAiStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="conversation_id", nullable=false)
    private Conversation conversation;

    @Column(name="ai_task_id")
    private Long aiTaskId;

    private Integer rank;

    @Column(precision=10, scale=4)
    private BigDecimal score;

    // Getters y setters
}
