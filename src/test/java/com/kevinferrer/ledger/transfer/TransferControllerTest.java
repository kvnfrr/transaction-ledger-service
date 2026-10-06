package com.kevinferrer.ledger.transfer;

import com.kevinferrer.ledger.transfer.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @Test
    void createsTransferAndReturns201() throws Exception {
        UUID transferId = UUID.randomUUID();
        UUID sourceId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();

        var source = mock(com.kevinferrer.ledger.account.Account.class);
        var destination = mock(com.kevinferrer.ledger.account.Account.class);
        var transfer = mock(Transfer.class);

        when(source.getId()).thenReturn(sourceId);
        when(destination.getId()).thenReturn(destinationId);

        when(transfer.getId()).thenReturn(transferId);
        when(transfer.getSourceAccount()).thenReturn(source);
        when(transfer.getDestinationAccount()).thenReturn(destination);
        when(transfer.getAmount()).thenReturn(new BigDecimal("250.00"));
        when(transfer.getCurrency()).thenReturn("USD");
        when(transfer.getCreatedAt())
                .thenReturn(Instant.parse("2026-10-06T20:00:00Z"));

        when(transferService.transfer(
                sourceId,
                destinationId,
                new BigDecimal("250.00")
        )).thenReturn(transfer);

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceAccountId": "%s",
                                  "destinationAccountId": "%s",
                                  "amount": 250.00
                                }
                                """.formatted(sourceId, destinationId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transferId.toString()))
                .andExpect(jsonPath("$.sourceAccountId").value(sourceId.toString()))
                .andExpect(jsonPath("$.destinationAccountId").value(destinationId.toString()))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.currency").value("USD"));
    }

    @Test
    void rejectsInvalidTransferAmount() throws Exception {
        UUID sourceId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "sourceAccountId": "%s",
                              "destinationAccountId": "%s",
                              "amount": 0
                            }
                            """.formatted(sourceId, destinationId)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transferService);
    }

    @Test
    void returns400WhenTransferIsRejected() throws Exception {
        UUID sourceId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();

        when(transferService.transfer(
                sourceId,
                destinationId,
                new BigDecimal("5000.00")
        )).thenThrow(new IllegalArgumentException("Insufficient funds"));

        mockMvc.perform(post("/api/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "sourceAccountId": "%s",
                              "destinationAccountId": "%s",
                              "amount": 5000.00
                            }
                            """.formatted(sourceId, destinationId)))
                .andExpect(status().isBadRequest());
    }
}