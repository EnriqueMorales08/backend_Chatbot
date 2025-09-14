package com.servicioiphone.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ChatbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatbotApplication.class, args);
		System.out.println("Aplicación iniciada 🚀");
	}
	@GetMapping("/hola")
	public String holaMundo() {
		return "¡Hola Mundo desde Spring Boot!";
	}

}
