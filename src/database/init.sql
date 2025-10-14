CREATE DATABASE IF NOT EXISTS vaudoise_db;
USE vaudoise_db;

CREATE TABLE IF NOT EXISTS vaudoise_clients (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL,
    client_type VARCHAR(50) NOT NULL,
    birth_date DATE,
    company_id VARCHAR(50) UNIQUE
);

CREATE TABLE IF NOT EXISTS vaudoise_contracts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    end_date DATE,
    cost DECIMAL(15,2) NOT NULL,
    client_id BIGINT,
    CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES vaudoise_clients(id) ON DELETE CASCADE
);

INSERT INTO vaudoise_clients (uuid, name, email, phone, client_type, birth_date)
VALUES (UUID(), 'John Doe', 'john@example.com', '+1234567890', 'PERSON', '1980-01-01');

INSERT INTO vaudoise_clients (uuid, name, email, phone, client_type, company_id)
VALUES (UUID(), 'Acme Corp', 'contact@acme.com', '+1234567899', 'COMPANY', 'ACME-123');
