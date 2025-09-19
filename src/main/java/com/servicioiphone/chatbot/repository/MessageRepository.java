package com.servicioiphone.chatbot.repository;

import com.servicioiphone.chatbot.model.Message;
import com.servicioiphone.chatbot.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByConversationOrderByCreatedAtAsc(Conversation conv, Pageable pageable);
    Message findTop1ByConversationOrderByCreatedAtDesc(Conversation conv);
    Message findByWaMessageId(String waMessageId);
}
