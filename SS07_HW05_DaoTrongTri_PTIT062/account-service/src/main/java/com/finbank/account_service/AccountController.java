package com.finbank.account_service;

import com.finbank.account_service.dto.AmountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @Autowired
    private Environment env;
    
    // Mock database: AccountNumber -> Map(balance, customerId)
    private static final Map<String, Map<String, Object>> accountData = new HashMap<>();
    static {
        Map<String, Object> a1 = new HashMap<>();
        a1.put("balance", 10000000L);
        a1.put("customerId", "123");
        
        Map<String, Object> a2 = new HashMap<>();
        a2.put("balance", 5000000L);
        a2.put("customerId", "456");
        
        accountData.put("1001", a1);
        accountData.put("1002", a2);
    }
    
    @GetMapping("/info")
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("port", env.getProperty("local.server.port"));
        info.put("service", "account-service");
        return info;
    }
    
    @GetMapping
    public String getAllAccounts() {
        return "List of accounts";
    }
    
    @GetMapping("/{accountNumber}")
    public ResponseEntity<Map<String, Object>> getAccount(@PathVariable String accountNumber) {
        if (!accountData.containsKey(accountNumber)) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> account = new HashMap<>();
        account.put("accountNumber", accountNumber);
        account.put("balance", accountData.get(accountNumber).get("balance"));
        account.put("customerId", accountData.get(accountNumber).get("customerId"));
        account.put("status", "ACTIVE");
        return ResponseEntity.ok(account);
    }
    
    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<Long> getBalance(@PathVariable String accountNumber) {
        if (!accountData.containsKey(accountNumber)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok((Long) accountData.get(accountNumber).get("balance"));
    }
    
    @PutMapping("/{accountNumber}/debit")
    public ResponseEntity<String> debit(@PathVariable String accountNumber, @RequestBody AmountDto dto) {
        if (!accountData.containsKey(accountNumber)) return ResponseEntity.badRequest().body("Account not found");
        
        Long currentBalance = (Long) accountData.get(accountNumber).get("balance");
        if (currentBalance < dto.getAmount()) return ResponseEntity.badRequest().body("Insufficient balance");
        
        accountData.get(accountNumber).put("balance", currentBalance - dto.getAmount());
        return ResponseEntity.ok("Debit successful");
    }
    
    @PutMapping("/{accountNumber}/credit")
    public ResponseEntity<String> credit(@PathVariable String accountNumber, @RequestBody AmountDto dto) {
        if (!accountData.containsKey(accountNumber)) return ResponseEntity.badRequest().body("Account not found");
        
        Long currentBalance = (Long) accountData.get(accountNumber).get("balance");
        accountData.get(accountNumber).put("balance", currentBalance + dto.getAmount());
        return ResponseEntity.ok("Credit successful");
    }
    
    // API moi cho Bai 5
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Map<String, Object>>> getAccountsByCustomer(@PathVariable String customerId) {
        List<Map<String, Object>> accounts = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : accountData.entrySet()) {
            if (customerId.equals(entry.getValue().get("customerId"))) {
                Map<String, Object> account = new HashMap<>();
                account.put("accountNumber", entry.getKey());
                account.put("balance", entry.getValue().get("balance"));
                account.put("customerId", customerId);
                account.put("status", "ACTIVE");
                accounts.add(account);
            }
        }
        return ResponseEntity.ok(accounts);
    }
}
