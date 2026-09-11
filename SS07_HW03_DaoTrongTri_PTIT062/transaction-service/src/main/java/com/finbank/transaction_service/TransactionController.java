package com.finbank.transaction_service;

import com.finbank.transaction_service.dto.AmountDto;
import com.finbank.transaction_service.dto.TransferRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final Map<String, Map<String, Object>> transactions = new HashMap<>();

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        try {
            // Kiểm tra tài khoản nguồn
            try {
                restTemplate.getForEntity("http://account-service/api/accounts/" + request.getFromAccountNumber(), Map.class);
            } catch (HttpClientErrorException.NotFound e) {
                return ResponseEntity.badRequest().body("FAILED: Tài khoản nguồn không tồn tại");
            }
            
            // Kiểm tra tài khoản đích
            try {
                restTemplate.getForEntity("http://account-service/api/accounts/" + request.getToAccountNumber(), Map.class);
            } catch (HttpClientErrorException.NotFound e) {
                return ResponseEntity.badRequest().body("FAILED: Tài khoản đích không tồn tại");
            }
            
            // Trừ/cộng tiền
            try {
                restTemplate.put("http://account-service/api/accounts/" + request.getFromAccountNumber() + "/debit", new AmountDto(request.getAmount()));
                restTemplate.put("http://account-service/api/accounts/" + request.getToAccountNumber() + "/credit", new AmountDto(request.getAmount()));
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("FAILED: Không đủ số dư");
            }
            
            String transactionId = UUID.randomUUID().toString();
            Map<String, Object> tx = new HashMap<>();
            tx.put("id", transactionId);
            tx.put("from", request.getFromAccountNumber());
            tx.put("to", request.getToAccountNumber());
            tx.put("amount", request.getAmount());
            tx.put("status", "SUCCESS");
            transactions.put(transactionId, tx);
            
            return ResponseEntity.ok("SUCCESS: Chuyển tiền thành công. Mã GD: " + transactionId);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("FAILED: Lỗi hệ thống " + e.getMessage());
        }
    }
}
