package com.servicioiphone.chatbot.service;

import com.servicioiphone.chatbot.dto.InboundWhatsappMessageRequest;
import com.servicioiphone.chatbot.model.*;
import com.servicioiphone.chatbot.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final ContactRepository contactRepo;
    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;

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

        // Actualizar último mensaje
        conversation.setLastMessageAt(Instant.now());
        conversationRepo.save(conversation);
    }
}
