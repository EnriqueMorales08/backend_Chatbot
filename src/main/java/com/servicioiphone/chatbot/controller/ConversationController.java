package com.servicioiphone.chatbot.controller;

import com.servicioiphone.chatbot.dto.ConversationSummaryDTO;
import com.servicioiphone.chatbot.dto.ConversationWithMessagesDTO;
import com.servicioiphone.chatbot.dto.MessageResponseDTO;
import com.servicioiphone.chatbot.model.Conversation;
import com.servicioiphone.chatbot.model.Direction;
import com.servicioiphone.chatbot.model.Message;
import com.servicioiphone.chatbot.model.MessageStatus;
import com.servicioiphone.chatbot.repository.ConversationRepository;
import com.servicioiphone.chatbot.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationRepository convRepo;
    private final MessageRepository msgRepo;

    /** 🔹 Lista de clientes con su último mensaje */
    @GetMapping("/clientes")
    public List<Map<String, Object>> getClientes() {
        return convRepo.findAll().stream().map(conv -> {
            Message lastMsg = msgRepo.findTop1ByConversationOrderByCreatedAtDesc(conv);

            // Calcular mensajes no leídos (status != READ)
            long mensajesNoLeidos = msgRepo.findByConversationOrderByCreatedAtAsc(conv, PageRequest.of(0, Integer.MAX_VALUE))
                    .getContent()
                    .stream()
                    .filter(msg -> msg.getStatus() != MessageStatus.READ)
                    .count();

            // Crear mapa con la estructura mejorada
            Map<String, Object> cliente = new HashMap<>();
            cliente.put("id", conv.getId());
            cliente.put("nombre", conv.getContact().getName());
            cliente.put("waId", conv.getContact().getWaId());
            cliente.put("mensajesNoLeidos", (int) mensajesNoLeidos);
            cliente.put("ultimaActividad", conv.getLastMessageAt());
            cliente.put("estado", "ACTIVE");

            // Último mensaje como objeto
            if (lastMsg != null) {
                Map<String, Object> ultimoMensaje = new HashMap<>();
                ultimoMensaje.put("texto", lastMsg.getContent());
                ultimoMensaje.put("hora", lastMsg.getCreatedAt().atZone(ZoneId.of("America/Lima")).toLocalTime().toString().substring(0, 5)); // HH:MM
                ultimoMensaje.put("fecha", lastMsg.getCreatedAt().atZone(ZoneId.of("America/Lima")).toLocalDate().toString());
                ultimoMensaje.put("status", lastMsg.getStatus() != null ? lastMsg.getStatus().toString() : null);
                ultimoMensaje.put("from_me", lastMsg.getDirection() == Direction.OUT); // true si es OUTbound
                cliente.put("ultimoMensaje", ultimoMensaje);
            } else {
                cliente.put("ultimoMensaje", null);
            }

            return cliente;
        }).toList();
    }

    /** 🔹 Mensajes de una conversación */
    @GetMapping("/conversaciones/{id}/messages")
    public ConversationWithMessagesDTO getMessages(@PathVariable Long id,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "50") int size) {
        Conversation conv = convRepo.findById(id).orElseThrow();
        List<Message> messages = msgRepo.findByConversationOrderByCreatedAtAsc(conv, PageRequest.of(page, size))
                .getContent();

        // Get last message content
        String ultimoMensaje = messages.isEmpty() ? "" : messages.get(messages.size() - 1).getContent();

        List<MessageResponseDTO> messageDTOs = messages.stream()
                .map(m -> {
                    // Convert content to array format
                    List<MessageResponseDTO.ContentItem> contentItems = List.of(
                            new MessageResponseDTO.ContentItem("text", "body", m.getContent())
                    );

                    // Format timestamps
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
                    String timestamp = m.getCreatedAt().atZone(ZoneId.of("UTC")).format(formatter);
                    String createdAt = m.getCreatedAt().toString() + ".000000Z";
                    String updatedAt = m.getUpdatedAt() != null ? m.getUpdatedAt().toString() + ".000000Z" : createdAt;

                    return new MessageResponseDTO(
                            m.getId(),
                            conv.getId(),
                            m.getDirection() == Direction.OUT,
                            m.getMessageType() != null ? m.getMessageType() : "text",
                            contentItems,
                            timestamp,
                            m.getStatus() != null ? m.getStatus().toString().toLowerCase() : "sent",
                            createdAt,
                            updatedAt
                    );
                })
                .toList();

        return new ConversationWithMessagesDTO(
                conv.getId(),
                conv.getContact().getName(),
                ultimoMensaje,
                messageDTOs
        );
    }

    /** 🔹 Actualizar estado de un mensaje */
    @PatchMapping("/messages/{messageId}/status")
    public ResponseEntity<Void> updateMessageStatus(@PathVariable Long messageId,
                                                   @RequestParam String status) {
        // Convertir status a enum (case-insensitive)
        MessageStatus messageStatus;
        try {
            messageStatus = MessageStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Status inválido
        }

        Message message = msgRepo.findById(messageId).orElseThrow();

        // 🚫 REGLA IMPORTANTE: Una vez que es READ, no se puede cambiar
        if (message.getStatus() == MessageStatus.READ) {
            return ResponseEntity.ok().build(); // No hacer cambios, pero devolver OK
        }

        // ✅ Solo permitir transiciones válidas
        if (isValidStatusTransition(message.getStatus(), messageStatus)) {
            message.setStatus(messageStatus);
            msgRepo.save(message);
        }

        return ResponseEntity.ok().build();
    }

    /** 🔹 Marcar todos los mensajes de una conversación como leídos */
    @PatchMapping("/conversaciones/{conversationId}/mark-read")
    public ResponseEntity<Void> markConversationAsRead(@PathVariable Long conversationId) {
        Conversation conv = convRepo.findById(conversationId).orElseThrow();
        List<Message> messages = msgRepo.findByConversationOrderByCreatedAtAsc(conv, PageRequest.of(0, Integer.MAX_VALUE))
                .getContent();

        messages.forEach(msg -> msg.setStatus(MessageStatus.READ));
        msgRepo.saveAll(messages);

        return ResponseEntity.ok().build();
    }

    /** 🔹 Actualizar estado de múltiples mensajes */
    @PatchMapping("/messages/bulk-status")
    public ResponseEntity<Void> updateBulkMessageStatus(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> messageIds = (List<Long>) request.get("messageIds");
        String statusStr = (String) request.get("status");

        // Convertir status a enum (case-insensitive)
        MessageStatus status;
        try {
            status = MessageStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Status inválido
        }

        List<Message> messages = msgRepo.findAllById(messageIds);
        messages.forEach(msg -> {
            // 🚫 Solo cambiar si no es READ y la transición es válida
            if (msg.getStatus() != MessageStatus.READ && isValidStatusTransition(msg.getStatus(), status)) {
                msg.setStatus(status);
            }
        });
        msgRepo.saveAll(messages);

        return ResponseEntity.ok().build();
    }

    /** 🔹 Marcar mensaje como entregado (DELIVERED) */
    @PatchMapping("/messages/{messageId}/delivered")
    public ResponseEntity<Void> markMessageAsDelivered(@PathVariable Long messageId) {
        return updateMessageStatus(messageId, "DELIVERED");
    }

    /** 🔹 Marcar mensaje como leído (READ) */
    @PatchMapping("/messages/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(@PathVariable Long messageId) {
        return updateMessageStatus(messageId, "READ");
    }

    // ========== ENDPOINTS USANDO WA_MESSAGE_ID (WhatsApp API) ==========

    /** 🔹 Actualizar estado usando waMessageId (WhatsApp) */
    @PatchMapping("/messages/wa/{waMessageId}/status")
    public ResponseEntity<Void> updateMessageStatusByWaId(@PathVariable String waMessageId,
                                                         @RequestParam String status) {
        // Decodificar el waMessageId si viene URL-encoded
        try {
            waMessageId = java.net.URLDecoder.decode(waMessageId, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Si falla la decodificación, usar el valor original
        }

        // Convertir status a enum (case-insensitive)
        MessageStatus messageStatus;
        try {
            messageStatus = MessageStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Status inválido
        }

        Message message = msgRepo.findByWaMessageId(waMessageId);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }

        // 🚫 REGLA IMPORTANTE: Una vez que es READ, no se puede cambiar
        if (message.getStatus() == MessageStatus.READ) {
            return ResponseEntity.ok().build(); // No hacer cambios, pero devolver OK
        }

        // ✅ Solo permitir transiciones válidas:
        // SENT → DELIVERED/READ
        // DELIVERED → READ
        // READ → No se permite cambiar (ya validado arriba)
        if (isValidStatusTransition(message.getStatus(), messageStatus)) {
            message.setStatus(messageStatus);
            msgRepo.save(message);
        }

        return ResponseEntity.ok().build();
    }

    /** 🔹 Marcar mensaje como entregado usando waMessageId */
    @PatchMapping("/messages/wa/{waMessageId}/delivered")
    public ResponseEntity<Void> markMessageAsDeliveredByWaId(@PathVariable String waMessageId) {
        // Decodificar el waMessageId si viene URL-encoded
        try {
            waMessageId = java.net.URLDecoder.decode(waMessageId, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Si falla la decodificación, usar el valor original
        }
        return updateMessageStatusByWaId(waMessageId, "DELIVERED");
    }

    /** 🔹 Marcar mensaje como leído usando waMessageId */
    @PatchMapping("/messages/wa/{waMessageId}/read")
    public ResponseEntity<Void> markMessageAsReadByWaId(@PathVariable String waMessageId) {
        // Decodificar el waMessageId si viene URL-encoded
        try {
            waMessageId = java.net.URLDecoder.decode(waMessageId, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Si falla la decodificación, usar el valor original
        }
        return updateMessageStatusByWaId(waMessageId, "READ");
    }

    /** 🔹 Actualizar múltiples mensajes usando waMessageIds */
    @PatchMapping("/messages/bulk-status-wa")
    public ResponseEntity<Void> updateBulkMessageStatusByWaIds(@RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<String> waMessageIds = (List<String>) request.get("waMessageIds");
        String statusStr = (String) request.get("status");

        // Convertir status a enum (case-insensitive)
        MessageStatus status;
        try {
            status = MessageStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Status inválido
        }

        List<Message> messages = waMessageIds.stream()
                .map(msgRepo::findByWaMessageId)
                .filter(java.util.Objects::nonNull)
                .toList();

        messages.forEach(msg -> {
            // 🚫 Solo cambiar si no es READ y la transición es válida
            if (msg.getStatus() != MessageStatus.READ && isValidStatusTransition(msg.getStatus(), status)) {
                msg.setStatus(status);
            }
        });
        msgRepo.saveAll(messages);

        return ResponseEntity.ok().build();
    }

    /**
     * 🔒 Valida las transiciones de estado permitidas:
     * - SENT → DELIVERED, READ
     * - DELIVERED → READ
     * - READ → No se permite cambiar (estado final)
     */
    private boolean isValidStatusTransition(MessageStatus currentStatus, MessageStatus newStatus) {
        if (currentStatus == null) {
            return true; // Permitir cualquier transición desde null
        }

        switch (currentStatus) {
            case SENT:
                return newStatus == MessageStatus.DELIVERED || newStatus == MessageStatus.READ;
            case DELIVERED:
                return newStatus == MessageStatus.READ;
            case READ:
                return false; // READ es estado final, no se puede cambiar
            default:
                return false;
        }
    }
}
