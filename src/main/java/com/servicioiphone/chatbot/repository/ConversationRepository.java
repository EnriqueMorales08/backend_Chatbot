package com.servicioiphone.chatbot.repository;

import com.servicioiphone.chatbot.model.Contact;
import com.servicioiphone.chatbot.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
  Optional<Conversation> findByContact(Contact contact);
}
