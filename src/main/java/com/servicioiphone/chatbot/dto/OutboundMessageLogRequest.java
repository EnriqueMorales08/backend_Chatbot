package com.servicioiphone.chatbot.dto;
import java.time.Instant;
public class OutboundMessageLogRequest {
    public Long organizationId;
    public Long channelAccountId;
    public Long conversationId;        // si ya existe; si no, pondremos contacto
    public Long contactId;
    public String providerMessageId;   // id que regresó Graph
    public String toPhone;             // cliente
    public String messageType;         // TEXT, IMAGE, ...
    public String text;                // si aplica
    public String mediaUrl;            // si aplica
    public String metadataJson;        // JSON opcional
    public Instant sentAt;
}