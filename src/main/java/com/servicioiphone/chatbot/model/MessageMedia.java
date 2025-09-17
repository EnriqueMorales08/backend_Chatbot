package com.servicioiphone.chatbot.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "message_media")
public class MessageMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name="message_id", nullable=false)
    private Message message;

    @Column(name="media_url", length=500, nullable=false)
    private String mediaUrl;

    @Column(name="media_type", length=80)
    private String mediaType;

    @Column(name="file_name", length=255)
    private String fileName;

    private Long size;
    private Integer width;
    private Integer height;
    private Integer duration;

    // Getters y setters
}
