package com.servicioiphone.chatbot.controller;

import com.servicioiphone.chatbot.dto.InboundWhatsappMessageRequest;
import com.servicioiphone.chatbot.service.MessagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class WebhookController {

    private final MessagingService messagingService;

    @PostMapping("/whatsapp")
    public ResponseEntity<Void> receiveWhatsapp(@RequestBody InboundWhatsappMessageRequest dto) {
        messagingService.registerFromN8n(dto);
        return ResponseEntity.accepted().build();
    }
}
