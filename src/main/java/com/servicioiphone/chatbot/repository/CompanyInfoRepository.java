package com.servicioiphone.chatbot.repository;

import com.servicioiphone.chatbot.model.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {
}