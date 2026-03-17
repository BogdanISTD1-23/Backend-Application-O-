package com.operator.subscriber.controller;

import com.operator.subscriber.payload.response.TransactionResponse;
import com.operator.subscriber.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> list(@RequestParam long subscriberId) {
        return ResponseEntity.ok(transactionService.listBySubscriberId(subscriberId));
    }
}

