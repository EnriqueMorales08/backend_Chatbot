package com.servicioiphone.chatbot.repository;
import com.servicioiphone.chatbot.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
}