package com.servicioiphone.chatbot.dto;
import java.time.Instant;
public class InboundWhatsappMessageRequest {
    public Long organizationId;
    public Long channelAccountId;
    public String provider;            // "whatsapp"
    public String providerMessageId;   // id del mensaje en WhatsApp
    public String fromPhone;           // cliente
    public String toPhone;             // tu número WABA
    public String messageType;         // TEXT, IMAGE, BUTTONS, etc.
    public String text;                // si aplica
    public String mediaUrl;            // si aplica
    public String metadataJson;        // JSON string opcional
    public String rawPayloadJson;      // JSON string completo
    public Instant sentAt;             // cuando WhatsApp lo emitió
}