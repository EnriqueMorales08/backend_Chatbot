INSERT INTO organizations (name, status, created_at, updated_at)
VALUES ('Mi Organización Demo', 'ACTIVE', NOW(), NOW());

INSERT INTO channel_accounts (organization_id, channel_code, display_name, provider_metadata, created_at, updated_at)
VALUES (1, 'WHATSAPP', 'Cuenta WhatsApp Demo', '{}', NOW(), NOW());
