package com.servicioiphone.chatbot.repository;

import com.servicioiphone.chatbot.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}