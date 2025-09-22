-- Contacto
INSERT INTO contact (wa_id, name, created_at, updated_at)
VALUES ('51987654321', 'Luis Arnaldo Chapa Morales', NOW(), NOW())
ON CONFLICT (wa_id) DO NOTHING;

-- Conversación (asociada al contacto recién insertado)
INSERT INTO conversation (contact_id, created_at, last_message_at)
VALUES (
  (SELECT id FROM contact WHERE wa_id = '51987654321'),
  NOW(),
  NOW()
) ON CONFLICT (contact_id) DO NOTHING;

-- Mensaje OUT (chatbot responde)
INSERT INTO chat_message (conversation_id, direction, content, wa_message_id, wa_timestamp, status, message_type, created_at, updated_at)
VALUES (
  (SELECT id FROM conversation WHERE contact_id = (SELECT id FROM contact WHERE wa_id = '51987654321')),
  'OUT',
  'Hola, ¿ya tienes lo que necesitas?',
  'wamid.msg1',
  NOW(),
  'SENT',
  'text',
  NOW(),
  NOW()
);

-- Mensaje IN (cliente responde)
INSERT INTO chat_message (conversation_id, direction, content, wa_message_id, wa_timestamp, status, message_type, created_at, updated_at)
VALUES (
  (SELECT id FROM conversation WHERE contact_id = (SELECT id FROM contact WHERE wa_id = '51987654321')),
  'IN',
  'Ya tengo lo que necesito es un repuesto',
  'wamid.msg2',
  NOW(),
  'SENT',
  'text',
  NOW(),
  NOW()
);

-- Otro mensaje IN (detalle del cliente)
INSERT INTO chat_message (conversation_id, direction, content, wa_message_id, wa_timestamp, status, message_type, created_at, updated_at)
VALUES (
  (SELECT id FROM conversation WHERE contact_id = (SELECT id FROM contact WHERE wa_id = '51987654321')),
  'IN',
  'La chapa de la maletera',
  'wamid.msg3',
  NOW(),
  'SENT',
  'text',
  NOW(),
  NOW()
);

-- User for authentication
INSERT INTO users (username, password, role, enabled, created_at, updated_at)
VALUES ('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'ADMIN', true, NOW(), NOW())
ON CONFLICT (username) DO NOTHING;

-- User Profile
INSERT INTO user_profiles (name, role, email, phone, avatar_url, created_at, updated_at)
VALUES ('SIMPLIFIQA Team', 'Administrador', 'gustavo@simplifiqa.com', '+51 999 888 777', 'https://i.pravatar.cc/200', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Company Info
INSERT INTO company_info (name, product, email, phone, created_at, updated_at)
VALUES ('SIMPLIFIQA', 'Leadly CRM', 'admin@simplifiqa.com', '+51 933 785 623', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Whatsapp Config
INSERT INTO whatsapp_config (business_number, greeting, created_at, updated_at)
VALUES ('+51 926 386 534', '¡Hola! Gracias por contactar con SIMPLIFIQA. ¿En qué podemos ayudarte?', NOW(), NOW())
ON CONFLICT DO NOTHING;
