package com.servicioiphone.chatbot.service;
import com.servicioiphone.chatbot.dto.InboundWhatsappMessageRequest;
import com.servicioiphone.chatbot.dto.OutboundMessageLogRequest;
import com.servicioiphone.chatbot.model.Organization;
import com.servicioiphone.chatbot.model.ChannelAccount;
import com.servicioiphone.chatbot.model.Contact;
import com.servicioiphone.chatbot.model.Conversation;
import com.servicioiphone.chatbot.model.Message;
import com.servicioiphone.chatbot.model.WebhookEvent;
import com.servicioiphone.chatbot.repository.OrganizationRepository;
import com.servicioiphone.chatbot.repository.ChannelAccountRepository;
import com.servicioiphone.chatbot.repository.ContactRepository;
import com.servicioiphone.chatbot.repository.ConversationRepository;
import com.servicioiphone.chatbot.repository.MessageRepository;
import com.servicioiphone.chatbot.repository.WebhookEventRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
@Service
@RequiredArgsConstructor
public class MessageIngressService {

    private final OrganizationRepository organizationRepo;
    private final ChannelAccountRepository channelAccountRepo;
    private final ContactRepository contactRepo;
    private final ConversationRepository conversationRepo;
    private final MessageRepository messageRepo;
    private final WebhookEventRepository webhookRepo;

    @Transactional
    public Map<String, Object> processInbound(InboundWhatsappMessageRequest req) {
        // 1) Validaciones mínimas
        Organization org = organizationRepo.findById(req.organizationId)
            .orElseThrow(() -> new IllegalArgumentException("organizationId inválido"));
        ChannelAccount channel = channelAccountRepo.findById(req.channelAccountId)
            .orElseThrow(() -> new IllegalArgumentException("channelAccountId inválido"));

        // 2) Contacto: find or create
        Contact contact = contactRepo.findByOrganization_IdAndPhone(org.getId(), req.fromPhone)
            .orElseGet(() -> {
                Contact c = new Contact();
                c.setOrganization(org);
                c.setFullName(null);
                c.setPhone(req.fromPhone);
                c.setSource("whatsapp");
                c.setCreatedAt(Instant.now());
                return contactRepo.save(c);
            });

        // 3) Conversation: find open or create
        Conversation conv = conversationRepo
            .findFirstByOrganization_IdAndContact_IdAndChannelAccount_IdAndStatusOrderByLastMessageAtDesc(
                org.getId(), contact.getId(), channel.getId(), "OPEN"
            )
            .orElseGet(() -> {
                Conversation cv = new Conversation();
                cv.setOrganization(org);
                cv.setContact(contact);
                cv.setChannelAccount(channel);
                cv.setStatus("OPEN");
                cv.setFirstMessageAt(Instant.now());
                cv.setLastMessageAt(Instant.now());
                cv.setCreatedAt(Instant.now());
                return conversationRepo.save(cv);
            });

        // 4) WebhookEvent (auditoría)
        WebhookEvent ev = new WebhookEvent();
        ev.setOrganization(org);
        ev.setChannelAccount(channel);
        ev.setEventType("INBOUND_MESSAGE");
        ev.setPayload(req.rawPayloadJson); // JSON string
        ev.setReceivedAt(Instant.now());
        webhookRepo.save(ev);

        // 5) Message
        Message msg = new Message();
        msg.setOrganization(org);
        msg.setConversation(conv);
        msg.setChannelAccount(channel);
        msg.setFromMe(false);
        msg.setProviderMessageId(req.providerMessageId);
        msg.setMessageType(req.messageType != null ? req.messageType : "TEXT");
        msg.setContent(req.text);
        msg.setStatus("RECEIVED"); // o PENDING si prefieres
        msg.setCreatedAt(Instant.now());
        msg.setUpdatedAt(Instant.now());
        messageRepo.save(msg);

        // 6) Actualiza lastMessageAt
        conv.setLastMessageAt(Instant.now());
        conversationRepo.save(conv);

        return Map.of(
            "conversationId", conv.getId(),
            "contactId", contact.getId(),
            "messageId", msg.getId()
        );
    }

    @Transactional
    public Map<String, Object> logOutbound(OutboundMessageLogRequest req) {
        Organization org = organizationRepo.findById(req.organizationId)
            .orElseThrow(() -> new IllegalArgumentException("organizationId inválido"));
        ChannelAccount channel = channelAccountRepo.findById(req.channelAccountId)
            .orElseThrow(() -> new IllegalArgumentException("channelAccountId inválido"));

        Conversation conv = null;
        if (req.conversationId != null) {
            conv = conversationRepo.findById(req.conversationId)
                    .orElseThrow(() -> new IllegalArgumentException("conversationId inválido"));
        } else if (req.contactId != null) {
            // abre una convo si no existe (útil cuando es el primer mensaje)
            Contact contact = contactRepo.findById(req.contactId)
                    .orElseThrow(() -> new IllegalArgumentException("contactId inválido"));
            conv = conversationRepo
                .findFirstByOrganization_IdAndContact_IdAndChannelAccount_IdAndStatusOrderByLastMessageAtDesc(
                    org.getId(), contact.getId(), channel.getId(), "OPEN"
                ).orElseGet(() -> {
                    Conversation cv = new Conversation();
                    cv.setOrganization(org);
                    cv.setContact(contact);
                    cv.setChannelAccount(channel);
                    cv.setStatus("OPEN");
                    cv.setFirstMessageAt(Instant.now());
                    cv.setLastMessageAt(Instant.now());
                    cv.setCreatedAt(Instant.now());
                    return conversationRepo.save(cv);
                });
        } else {
            throw new IllegalArgumentException("Se requiere conversationId o contactId");
        }

        Message msg = new Message();
        msg.setOrganization(org);
        msg.setConversation(conv);
        msg.setChannelAccount(channel);
        msg.setFromMe(true);
        msg.setProviderMessageId(req.providerMessageId);
        msg.setMessageType(req.messageType != null ? req.messageType : "TEXT");
        msg.setContent(req.text);
        msg.setStatus("SENT");
        msg.setCreatedAt(req.sentAt != null ? req.sentAt : Instant.now());
        msg.setUpdatedAt(Instant.now());
        messageRepo.save(msg);

        conv.setLastMessageAt(Instant.now());
        conversationRepo.save(conv);

        return Map.of(
            "conversationId", conv.getId(),
            "messageId", msg.getId()
        );
    }
}
