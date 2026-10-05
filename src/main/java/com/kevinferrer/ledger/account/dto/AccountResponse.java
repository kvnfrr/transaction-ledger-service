package com.kevinferrer.ledger.account.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountResponse(
        UUID id,
        String ownerName,
        String currency,
        BigDecimal balance,
        Instant createdAt
) {
}