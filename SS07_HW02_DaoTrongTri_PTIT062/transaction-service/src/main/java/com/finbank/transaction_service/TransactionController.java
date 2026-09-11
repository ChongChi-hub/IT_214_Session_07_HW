package com.finbank.transaction_service;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    @GetMapping
    public String getTransactions() { return "List of transactions"; }
    @PostMapping
    public String createTransaction() { return "Transaction created"; }
}
