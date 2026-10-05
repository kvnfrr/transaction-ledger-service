package com.kevinferrer.ledger.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(

        @NotBlank
        String ownerName,

        @NotBlank
        String currency,

        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal openingBalance

) {
}