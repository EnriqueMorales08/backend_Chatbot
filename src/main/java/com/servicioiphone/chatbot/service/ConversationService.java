package com.servicioiphone.chatbot.service;


import com.servicioiphone.chatbot.model.Contact;
import com.servicioiphone.chatbot.model.Conversation;
import com.servicioiphone.chatbot.model.Message;
import com.servicioiphone.chatbot.model.Direction;
import com.servicioiphone.chatbot.model.MessageStatus;

import com.servicioiphone.chatbot.repository.ConversationRepository;
import com.servicioiphone.chatbot.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@RequiredArgsConstructor
public class ConversationService {
  private final ConversationRepository convRepo;
  private final MessageRepository msgRepo;

  @Transactional
  public Conversation findOrCreate(Contact contact) {
    return convRepo.findByContact(contact)
        .orElseGet(() -> convRepo.save(Conversation.builder()
            .contact(contact).lastMessageAt(Instant.now()).build()));
  }

  @Transactional
  public Message appendMessage(Conversation conv, Direction dir, String content,
                               String waMessageId, Instant waTs, MessageStatus status) {
    Message m = Message.builder()
        .conversation(conv).direction(dir).content(content)
        .waMessageId(waMessageId).waTimestamp(waTs).status(status)
        .build();
    Message saved = msgRepo.save(m);
    conv.setLastMessageAt(Instant.now());
    convRepo.save(conv);
    return saved;
  }
}