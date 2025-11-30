-- seed.sql - For initial Docker database setup

-- This file is loaded when PostgreSQL container starts for the first time
-- Flyway migrations will handle the actual schema in production

-- The schema is created by Flyway migrations
-- This file can contain additional demo data if needed

-- Demo session
INSERT INTO chat_sessions (id, user_identifier, is_active, created_at, last_activity_at) 
VALUES ('demo-session-001', 'demo-user', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

-- Demo messages
INSERT INTO chat_messages (id, session_id, content, sender_type, created_at) 
VALUES 
    ('msg-001', 'demo-session-001', 'Hello!', 'USER', CURRENT_TIMESTAMP),
    ('msg-002', 'demo-session-001', 'Hello! How can I help you today?', 'BOT', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;
