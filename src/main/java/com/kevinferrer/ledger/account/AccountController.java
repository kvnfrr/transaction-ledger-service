package com.kevinferrer.ledger.account;

import com.kevinferrer.ledger.account.dto.AccountResponse;
import com.kevinferrer.ledger.account.dto.CreateAccountRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        Account account = accountService.createAccount(
                request.ownerName(),
                request.currency(),
                request.openingBalance()
        );

        return new AccountResponse(
                account.getId(),
                account.getOwnerName(),
                account.getCurrency(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
}