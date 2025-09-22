package com.servicioiphone.chatbot.repository;

import com.servicioiphone.chatbot.model.WhatsappConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhatsappConfigRepository extends JpaRepository<WhatsappConfig, Long> {
}