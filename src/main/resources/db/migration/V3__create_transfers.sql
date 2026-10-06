CREATE TABLE transfers (
    id UUID PRIMARY KEY,
    source_account_id UUID NOT NULL,
    destination_account_id UUID NOT NULL,
    amount NUMERIC(19,2) NOT NULL,
    currency VARCHAR(3) NOt NULL,
    created_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_transfer_source
                       FOREIGN KEY (source_account_id) REFERENCES accounts(id),

    CONSTRAINT fk_transfer_destination
                       FOREIGN KEY (destination_account_id) REFERENCES  accounts(id)
);