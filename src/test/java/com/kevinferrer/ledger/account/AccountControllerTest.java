package com.kevinferrer.ledger.account;

import com.kevinferrer.ledger.common.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Test
    void createsAccountAndReturns201() throws Exception {
        UUID accountId = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-10-05T14:00:00Z");

        Account account = mock(Account.class);

        when(account.getId()).thenReturn(accountId);
        when(account.getOwnerName()).thenReturn("Corvo Attano");
        when(account.getCurrency()).thenReturn("USD");
        when(account.getBalance())
                .thenReturn(new BigDecimal("1000.00"));
        when(account.getCreatedAt()).thenReturn(createdAt);

        when(accountService.createAccount(
                "Corvo Attano",
                "USD",
                new BigDecimal("1000.00")
        )).thenReturn(account);

        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "ownerName": "Corvo Attano",
                              "currency": "USD",
                              "openingBalance": 1000.00
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id")
                        .value(accountId.toString()))
                .andExpect(jsonPath("$.ownerName")
                        .value("Corvo Attano"))
                .andExpect(jsonPath("$.currency")
                        .value("USD"))
                .andExpect(jsonPath("$.balance")
                        .value(1000.00))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-10-05T14:00:00Z"));

        verify(accountService).createAccount(
                "Corvo Attano",
                "USD",
                new BigDecimal("1000.00")
        );
    }

    @Test
    void rejectsInvalidAccountRequest() throws Exception {
        mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "ownerName": "",
                              "currency": "USD",
                              "openingBalance": -10.00
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void returnsAccountById() throws Exception {
        UUID accountId = UUID.randomUUID();
        Instant createdAt =
                Instant.parse("2026-10-05T14:00:00Z");

        Account account = mock(Account.class);

        when(account.getId()).thenReturn(accountId);
        when(account.getOwnerName()).thenReturn("Corvo Attano");
        when(account.getCurrency()).thenReturn("USD");
        when(account.getBalance())
                .thenReturn(new BigDecimal("1000.00"));
        when(account.getCreatedAt()).thenReturn(createdAt);

        when(accountService.getAccount(accountId))
                .thenReturn(account);

        mockMvc.perform(get("/api/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id")
                        .value(accountId.toString()))
                .andExpect(jsonPath("$.ownerName")
                        .value("Corvo Attano"))
                .andExpect(jsonPath("$.currency")
                        .value("USD"))
                .andExpect(jsonPath("$.balance")
                        .value(1000.00))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-10-05T14:00:00Z"));

        verify(accountService).getAccount(accountId);
    }

    @Test
    void returns404WhenAccountDoesNotExist() throws Exception {
        UUID accountId = UUID.randomUUID();

        when(accountService.getAccount(accountId))
                .thenThrow(new AccountNotFoundException(accountId));

        mockMvc.perform(get("/api/accounts/{id}", accountId))
                .andExpect(status().isNotFound());

        verify(accountService).getAccount(accountId);
    }
}