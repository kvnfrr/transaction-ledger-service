CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    owner_name VARCHAR(255) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance NUMERIC(19,2) NOT NULL,
    created_at TIMESTAMP NOT NULL
);