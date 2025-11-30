-- V1__Initial_Schema.sql

CREATE TABLE IF NOT EXISTS chat_sessions (
    id VARCHAR(255) PRIMARY KEY,
    user_identifier VARCHAR(255) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    context_data TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_activity_at TIMESTAMP
);

CREATE INDEX idx_sessions_user ON chat_sessions(user_identifier);
CREATE INDEX idx_sessions_active ON chat_sessions(is_active);

CREATE TABLE IF NOT EXISTS chat_messages (
    id VARCHAR(255) PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    sender_type VARCHAR(50) NOT NULL,
    intent VARCHAR(255),
    confidence_score DOUBLE PRECISION,
    entities TEXT,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (session_id) REFERENCES chat_sessions(id) ON DELETE CASCADE
);

CREATE INDEX idx_messages_session ON chat_messages(session_id);
CREATE INDEX idx_messages_created ON chat_messages(created_at);

CREATE TABLE IF NOT EXISTS faqs (
    id BIGSERIAL PRIMARY KEY,
    intent VARCHAR(255) NOT NULL UNIQUE,
    patterns TEXT NOT NULL,
    responses TEXT NOT NULL,
    context_required VARCHAR(255),
    quick_replies TEXT,
    priority INTEGER DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_faqs_intent ON faqs(intent);
CREATE INDEX idx_faqs_active ON faqs(is_active);
CREATE INDEX idx_faqs_priority ON faqs(priority DESC);

CREATE TABLE IF NOT EXISTS model_metadata (
    id BIGSERIAL PRIMARY KEY,
    model_name VARCHAR(255) NOT NULL,
    model_version VARCHAR(255) NOT NULL,
    model_path VARCHAR(500) NOT NULL,
    accuracy DOUBLE PRECISION,
    training_samples INTEGER,
    is_active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_model_active ON model_metadata(is_active);
