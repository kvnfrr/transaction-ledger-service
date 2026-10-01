package com.kevinferrer.ledger.account;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void savesAndFindsAccountById() {
        Account account = new Account(
                "Corvo Attano",
                "USD",
                new BigDecimal("1000.00")
        );

        Account savedAccount = accountRepository.save(account);
        UUID accountId = savedAccount.getId();

        entityManager.flush();
        entityManager.clear();

        Account foundAccount = accountRepository.findById(accountId)
                .orElseThrow();

        assertThat(foundAccount.getId()).isEqualTo(accountId);
        assertThat(foundAccount.getOwnerName()).isEqualTo("Corvo Attano");
        assertThat(foundAccount.getCurrency()).isEqualTo("USD");
        assertThat(foundAccount.getBalance())
                .isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(foundAccount.getCreatedAt()).isNotNull();
    }
}