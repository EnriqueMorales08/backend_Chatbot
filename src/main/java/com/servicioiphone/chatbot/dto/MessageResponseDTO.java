package com.servicioiphone.chatbot.dto;

import com.servicioiphone.chatbot.model.Direction;
import java.time.Instant;

public record MessageResponseDTO(
        Long id,
        Direction direction,
        String content,
        Instant createdAt
) {}