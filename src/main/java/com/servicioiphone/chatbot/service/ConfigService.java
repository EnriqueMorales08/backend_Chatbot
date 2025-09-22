package com.servicioiphone.chatbot.service;

import com.servicioiphone.chatbot.model.CompanyInfo;
import com.servicioiphone.chatbot.model.UserProfile;
import com.servicioiphone.chatbot.model.WhatsappConfig;
import com.servicioiphone.chatbot.repository.CompanyInfoRepository;
import com.servicioiphone.chatbot.repository.UserProfileRepository;
import com.servicioiphone.chatbot.repository.WhatsappConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfigService {

    private final UserProfileRepository userProfileRepository;
    private final CompanyInfoRepository companyInfoRepository;
    private final WhatsappConfigRepository whatsappConfigRepository;

    public Map<String, Object> getConfiguration() {
        UserProfile userProfile = userProfileRepository.findAll().stream().findFirst().orElse(null);
        CompanyInfo companyInfo = companyInfoRepository.findAll().stream().findFirst().orElse(null);
        WhatsappConfig whatsappConfig = whatsappConfigRepository.findAll().stream().findFirst().orElse(null);

        return Map.of(
                "user", userProfile,
                "company", companyInfo,
                "whatsapp", whatsappConfig
        );
    }
}