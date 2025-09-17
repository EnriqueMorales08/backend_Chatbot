package com.servicioiphone.chatbot.repository;
import com.servicioiphone.chatbot.model.WebhookEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventRepository extends JpaRepository<WebhookEvent, Long> {
}