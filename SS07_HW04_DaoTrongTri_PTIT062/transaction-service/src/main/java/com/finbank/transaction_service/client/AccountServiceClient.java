package com.finbank.transaction_service.client;

import com.finbank.transaction_service.dto.AmountDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@FeignClient(name = "account-service")
public interface AccountServiceClient {
    
    @GetMapping("/api/accounts/{accountNumber}")
    ResponseEntity<Map<String, Object>> getAccount(@PathVariable("accountNumber") String accountNumber);
    
    @GetMapping("/api/accounts/{accountNumber}/balance")
    ResponseEntity<Long> getBalance(@PathVariable("accountNumber") String accountNumber);
    
    @PutMapping("/api/accounts/{accountNumber}/debit")
    ResponseEntity<String> debit(@PathVariable("accountNumber") String accountNumber, @RequestBody AmountDto amount);
    
    @PutMapping("/api/accounts/{accountNumber}/credit")
    ResponseEntity<String> credit(@PathVariable("accountNumber") String accountNumber, @RequestBody AmountDto amount);
}
