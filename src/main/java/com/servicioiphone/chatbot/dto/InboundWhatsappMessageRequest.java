package com.servicioiphone.chatbot.dto;

import com.servicioiphone.chatbot.model.Direction;
import lombok.Data;

import java.time.Instant;

@Data
public class InboundWhatsappMessageRequest {
    private String waId;        // número WhatsApp
    private String name;        // nombre del contacto
    private String mensaje;     // texto del mensaje
    private Direction direction; // IN o OUT
    private String waMessageId; // id de WhatsApp
    private Instant waTimestamp; // timestamp
}
