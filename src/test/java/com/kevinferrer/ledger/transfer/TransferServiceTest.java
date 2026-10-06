package com.kevinferrer.ledger.transfer;

import com.kevinferrer.ledger.account.Account;
import com.kevinferrer.ledger.account.AccountNotFoundException;
import com.kevinferrer.ledger.account.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransferRepository transferRepository;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        transferService =
                new TransferService(accountRepository, transferRepository);
    }

    @Test
    void transfersFundsBetweenAccounts() {
        UUID sourceId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();

        Account source = new Account(
                "Alice",
                "USD",
                new BigDecimal("1000.00")
        );

        Account destination = new Account(
                "Bob",
                "USD",
                new BigDecimal("200.00")
        );

        when(accountRepository.findById(sourceId))
                .thenReturn(Optional.of(source));

        when(accountRepository.findById(destinationId))
                .thenReturn(Optional.of(destination));

        when(transferRepository.save(any(Transfer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Transfer transfer = transferService.transfer(
                sourceId,
                destinationId,
                new BigDecimal("250.00")
        );

        assertThat(source.getBalance())
                .isEqualByComparingTo("750.00");

        assertThat(destination.getBalance())
                .isEqualByComparingTo("450.00");

        assertThat(transfer.getAmount())
                .isEqualByComparingTo("250.00");

        assertThat(transfer.getCurrency()).isEqualTo("USD");

        verify(transferRepository).save(any(Transfer.class));
    }

    @Test
    void rejectsNonPositiveTransferAmount() {
        assertThatThrownBy(() -> transferService.transfer(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.ZERO
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transfer amount must be greater than zero");

        verifyNoInteractions(accountRepository, transferRepository);
    }

    @Test
    void rejectsTransferToSameAccount() {
        UUID accountId = UUID.randomUUID();

        assertThatThrownBy(() -> transferService.transfer(
                accountId,
                accountId,
                new BigDecimal("100.00")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Source and destination accounts must be different");

        verifyNoInteractions(accountRepository, transferRepository);
    }

    @Test
    void rejectsTransferWhenSourceAccountDoesNotExist() {
        UUID sourceId = UUID.randomUUID();

        when(accountRepository.findById(sourceId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.transfer(
                sourceId,
                UUID.randomUUID(),
                new BigDecimal("100.00")
        ))
                .isInstanceOf(AccountNotFoundException.class);

        verifyNoInteractions(transferRepository);
    }

    @Test
    void rejectsTransferBetweenDifferentCurrencies() {
        UUID sourceId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();

        Account source = new Account(
                "Alice",
                "USD",
                new BigDecimal("1000.00")
        );

        Account destination = new Account(
                "Bob",
                "EUR",
                new BigDecimal("200.00")
        );

        when(accountRepository.findById(sourceId))
                .thenReturn(Optional.of(source));

        when(accountRepository.findById(destinationId))
                .thenReturn(Optional.of(destination));

        assertThatThrownBy(() -> transferService.transfer(
                sourceId,
                destinationId,
                new BigDecimal("100.00")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Accounts must use the same currency");

        verifyNoInteractions(transferRepository);
    }

    @Test
    void rejectsTransferWithInsufficientFunds() {
        UUID sourceId = UUID.randomUUID();
        UUID destinationId = UUID.randomUUID();

        Account source = new Account(
                "Alice",
                "USD",
                new BigDecimal("50.00")
        );

        Account destination = new Account(
                "Bob",
                "USD",
                new BigDecimal("200.00")
        );

        when(accountRepository.findById(sourceId))
                .thenReturn(Optional.of(source));

        when(accountRepository.findById(destinationId))
                .thenReturn(Optional.of(destination));

        assertThatThrownBy(() -> transferService.transfer(
                sourceId,
                destinationId,
                new BigDecimal("100.00")
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient funds");

        assertThat(source.getBalance())
                .isEqualByComparingTo("50.00");

        assertThat(destination.getBalance())
                .isEqualByComparingTo("200.00");

        verifyNoInteractions(transferRepository);
    }
}