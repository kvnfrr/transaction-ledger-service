package com.kevinferrer.ledger.account;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(
            String ownerName,
            String currency,
            BigDecimal openingBalance
    ) {
        if (ownerName == null || ownerName.isBlank()) {
            throw new IllegalArgumentException(
                    "Owner name must not be blank"
            );
        }

        if (!"USD".equals(currency)) {
            throw new IllegalArgumentException(
                    "Only USD accounts are supported"
            );
        }

        if (openingBalance == null ||
                openingBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Opening balance must be zero or greater"
            );
        }

        Account account =
                new Account(ownerName, currency, openingBalance);

        return accountRepository.save(account);
    }

    public Account getAccount(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }
}