package com.kevinferrer.ledger.transfer.dto;

import com.kevinferrer.ledger.transfer.Transfer;
import com.kevinferrer.ledger.transfer.TransferService;
import com.kevinferrer.ledger.transfer.dto.CreateTransferRequest;
import com.kevinferrer.ledger.transfer.dto.TransferResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse createTransfer(
            @Valid @RequestBody CreateTransferRequest request
    ) {
        Transfer transfer = transferService.transfer(
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.amount()
        );

        return toResponse(transfer);
    }

    private TransferResponse toResponse(Transfer transfer) {
        return new TransferResponse(
                transfer.getId(),
                transfer.getSourceAccount().getId(),
                transfer.getDestinationAccount().getId(),
                transfer.getAmount(),
                transfer.getCurrency(),
                transfer.getCreatedAt()
        );
    }
}