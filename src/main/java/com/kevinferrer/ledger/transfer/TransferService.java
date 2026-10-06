package com.kevinferrer.ledger.transfer;

import com.kevinferrer.ledger.account.Account;
import com.kevinferrer.ledger.account.AccountNotFoundException;
import com.kevinferrer.ledger.account.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public TransferService(
            AccountRepository accountRepository,
            TransferRepository transferRepository
    ) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public Transfer transfer(
            UUID sourceAccountId,
            UUID destinationAccountId,
            BigDecimal amount
    ) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero"
            );
        }

        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalArgumentException(
                    "Source and destination accounts must be different"
            );
        }

        Account source = accountRepository.findById(sourceAccountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(sourceAccountId));

        Account destination = accountRepository.findById(destinationAccountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(destinationAccountId));

        if (!source.getCurrency().equals(destination.getCurrency())) {
            throw new IllegalArgumentException(
                    "Accounts must use the same currency"
            );
        }

        if (source.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        source.debit(amount);
        destination.credit(amount);

        Transfer transfer = new Transfer(
                source,
                destination,
                amount,
                source.getCurrency()
        );

        return transferRepository.save(transfer);
    }
}