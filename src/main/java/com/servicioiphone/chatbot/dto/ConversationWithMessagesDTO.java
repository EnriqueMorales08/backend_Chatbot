package com.servicioiphone.chatbot.dto;

import java.util.List;

public record ConversationWithMessagesDTO(
        Long id,
        String nombre,
        String ultimoMensaje,
        List<MessageResponseDTO> mensajes
) {}