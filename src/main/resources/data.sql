-- Contacto
INSERT INTO contact (wa_id, name, created_at, updated_at)
VALUES ('51987654321', 'Luis Arnaldo Chapa Morales', NOW(), NOW());

-- Conversación (asociada al contacto recién insertado)
INSERT INTO conversation (contact_id, created_at, last_message_at)
VALUES (
  (SELECT id FROM contact WHERE wa_id = '51987654321'),
  NOW(),
  NOW()
);

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
