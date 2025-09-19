package com.servicioiphone.chatbot.dto;

import java.time.Instant;

public record ConversationSummaryDTO(
        Long conversationId,
        String contactName,
        String waId,
        String lastMessage,
        Instant lastMessageAt
) {}
