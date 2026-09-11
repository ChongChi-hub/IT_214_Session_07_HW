package com.finbank.account_service;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @GetMapping
    public String getAllAccounts() { return "List of accounts"; }
    @PostMapping
    public String createAccount() { return "Account created"; }
}
