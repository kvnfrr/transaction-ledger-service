package com.kevinferrer.ledger.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository);
    }

    @Test
    void createsAccountWithValidInput() {
        when(accountRepository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountService.createAccount(
                "Corvo Attano",
                "USD",
                new BigDecimal("1000.00")
        );

        assertThat(result.getOwnerName())
                .isEqualTo("Corvo Attano");

        assertThat(result.getCurrency())
                .isEqualTo("USD");

        assertThat(result.getBalance())
                .isEqualByComparingTo(
                        new BigDecimal("1000.00")
                );

        verify(accountRepository)
                .save(any(Account.class));
    }

    @Test
    void rejectsBlankOwnerName() {
        assertThatThrownBy(() ->
                accountService.createAccount(
                        "   ",
                        "USD",
                        new BigDecimal("1000.00")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Owner name must not be blank");

        verifyNoInteractions(accountRepository);
    }

    @Test
    void rejectsUnsupportedCurrency() {
        assertThatThrownBy(() ->
                accountService.createAccount(
                        "Corvo Attano",
                        "EUR",
                        new BigDecimal("1000.00")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only USD accounts are supported");

        verifyNoInteractions(accountRepository);
    }

    @Test
    void rejectsNegativeOpeningBalance() {
        assertThatThrownBy(() ->
                accountService.createAccount(
                        "Corvo Attano",
                        "USD",
                        new BigDecimal("-1.00")
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Opening balance must be zero or greater"
                );

        verifyNoInteractions(accountRepository);
    }

    @Test
    void rejectsNullOpeningBalance() {
        assertThatThrownBy(() ->
                accountService.createAccount(
                        "Corvo Attano",
                        "USD",
                        null
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(
                        "Opening balance must be zero or greater"
                );

        verifyNoInteractions(accountRepository);
    }
}