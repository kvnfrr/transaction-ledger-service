package com.kevinferrer.ledger.transfer.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferResponse(
        UUID id,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency,
        Instant createdAt
) {
}