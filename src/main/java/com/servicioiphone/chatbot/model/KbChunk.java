package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "kb_chunks")
public class KbChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="document_id", nullable=false)
    private KbDocument document;

    @Column(columnDefinition="text", nullable=false)
    private String content;

    @Column(name="tokens_count")
    private Integer tokensCount;

    @Column(name="chunk_index")
    private Integer chunkIndex;

    @Column(columnDefinition="text")
    private String metadata; // JSON

    // Getters y setters
}
