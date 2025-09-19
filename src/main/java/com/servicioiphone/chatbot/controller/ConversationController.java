package com.servicioiphone.chatbot.controller;

import com.servicioiphone.chatbot.dto.ConversationSummaryDTO;
import com.servicioiphone.chatbot.dto.MessageResponseDTO;
import com.servicioiphone.chatbot.model.Conversation;
import com.servicioiphone.chatbot.model.Message;
import com.servicioiphone.chatbot.repository.ConversationRepository;
import com.servicioiphone.chatbot.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationRepository convRepo;
    private final MessageRepository msgRepo;

    /** 🔹 Lista de clientes con su último mensaje */
    @GetMapping("/clientes")
    public List<ConversationSummaryDTO> getClientes() {
        return convRepo.findAll().stream().map(conv -> {
            Message lastMsg = msgRepo.findTop1ByConversationOrderByCreatedAtDesc(conv);
            return new ConversationSummaryDTO(
                    conv.getId(),
                    conv.getContact().getName(),
                    conv.getContact().getWaId(),
                    lastMsg != null ? lastMsg.getContent() : "",
                    conv.getLastMessageAt()
            );
        }).toList();
    }

    /** 🔹 Mensajes de una conversación */
    @GetMapping("/conversaciones/{id}/messages")
    public List<MessageResponseDTO> getMessages(@PathVariable Long id,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "50") int size) {
        Conversation conv = convRepo.findById(id).orElseThrow();
        return msgRepo.findByConversationOrderByCreatedAtAsc(conv, PageRequest.of(page, size))
                .getContent()
                .stream()
                .map(m -> new MessageResponseDTO(
                        m.getId(),
                        m.getDirection(),
                        m.getContent(),
                        m.getCreatedAt()
                ))
                .toList();
    }
}
