package com.servicioiphone.chatbot.controller;

import com.servicioiphone.chatbot.dto.InboundWhatsappMessageRequest;
import com.servicioiphone.chatbot.dto.OutboundMessageLogRequest;
import com.servicioiphone.chatbot.service.MessageIngressService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WhatsappWebhookController {

    private final MessageIngressService ingressService;

    // 📥 Endpoint para recibir mensajes entrantes de WhatsApp (desde n8n → backend)
    @PostMapping("/webhooks/whatsapp")
    public ResponseEntity<Map<String, Object>> inbound(@RequestBody InboundWhatsappMessageRequest body) {
        var result = ingressService.processInbound(body);
        return ResponseEntity.ok(result);
    }

    // 📤 Endpoint para registrar mensajes salientes (backend → n8n → WhatsApp)
    @PostMapping("/messages/outbound")
    public ResponseEntity<Map<String, Object>> outbound(@RequestBody OutboundMessageLogRequest body) {
        var result = ingressService.logOutbound(body);
        return ResponseEntity.ok(result);
    }
}
