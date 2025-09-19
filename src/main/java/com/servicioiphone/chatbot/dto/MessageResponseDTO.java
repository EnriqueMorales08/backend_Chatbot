package com.servicioiphone.chatbot.dto;

import com.servicioiphone.chatbot.model.Direction;
import com.servicioiphone.chatbot.model.MessageStatus;
import java.time.Instant;
import java.util.List;

public record MessageResponseDTO(
        Long id,
        Long conversationId,
        boolean fromMe,
        String messageType,
        List<ContentItem> content,
        String timestamp,
        String status,
        String createdAt,
        String updatedAt
) {
    public record ContentItem(
            String type,
            String component,
            String text
    ) {}
}