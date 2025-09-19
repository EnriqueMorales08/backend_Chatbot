package com.servicioiphone.chatbot.service;

// service/ContactService.java

import com.servicioiphone.chatbot.model.Contact;
import com.servicioiphone.chatbot.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class ContactService {
  private final ContactRepository repo;

  public Contact findOrCreate(String waId, String name) {
    return repo.findByWaId(waId).orElseGet(() ->
      repo.save(Contact.builder().waId(waId).name(name).build()));
  }

  public void maybeUpdateName(Contact c, String newName) {
    if (newName != null && !newName.isBlank() && (c.getName()==null || !c.getName().equals(newName))) {
      c.setName(newName);
      repo.save(c);
    }
  }
}
