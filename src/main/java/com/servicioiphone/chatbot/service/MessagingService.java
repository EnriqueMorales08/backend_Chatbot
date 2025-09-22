package com.servicioiphone.chatbot.service;

import com.servicioiphone.chatbot.dto.InboundWhatsappMessageRequest;
import com.servicioiphone.chatbot.dto.MessageResponseDTO;
import com.servicioiphone.chatbot.model.*;
import com.servicioiphone.chatbot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final ContactRepository contactRepo;
    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;
    private final SimpMessagingTemplate messagingTemplate;

    public void registerFromN8n(InboundWhatsappMessageRequest dto) {
        // Buscar o crear contacto
        Contact contact = contactRepo.findByWaId(dto.getWaId())
                .orElseGet(() -> contactRepo.save(
                        Contact.builder().waId(dto.getWaId()).name(dto.getName()).build()
                ));

        // Buscar o crear conversación
        Conversation conversation = conversationRepo.findByContact(contact)
                .orElseGet(() -> conversationRepo.save(
                        Conversation.builder().contact(contact).lastMessageAt(Instant.now()).build()
                ));

        // Guardar mensaje
        Message msg = Message.builder()
                .conversation(conversation)
                .direction(dto.getDirection() != null ? dto.getDirection() : Direction.IN)
                .content(dto.getMensaje())
                .waMessageId(dto.getWaMessageId())
                .waTimestamp(dto.getWaTimestamp())
                .status(MessageStatus.SENT)
                .build();

        messageRepo.save(msg);

        // Crear DTO para WebSocket
        List<MessageResponseDTO.ContentItem> contentItems = List.of(
                new MessageResponseDTO.ContentItem("text", "body", msg.getContent())
        );

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        String timestamp = msg.getCreatedAt().atZone(ZoneId.of("UTC")).format(formatter);
        String createdAt = msg.getCreatedAt().toString() + ".000000Z";
        String updatedAt = msg.getUpdatedAt() != null ? msg.getUpdatedAt().toString() + ".000000Z" : createdAt;

        MessageResponseDTO messageDTO = new MessageResponseDTO(
                msg.getId(),
                conversation.getId(),
                msg.getDirection() == Direction.OUT,
                msg.getMessageType() != null ? msg.getMessageType() : "text",
                contentItems,
                timestamp,
                msg.getStatus() != null ? msg.getStatus().toString().toLowerCase() : "sent",
                createdAt,
                updatedAt
        );

        // Enviar mensaje por WebSocket
        messagingTemplate.convertAndSend("/topic/conversations/" + conversation.getId() + "/messages", messageDTO);

        // Actualizar último mensaje
        conversation.setLastMessageAt(Instant.now());
        conversationRepo.save(conversation);
    }
}
