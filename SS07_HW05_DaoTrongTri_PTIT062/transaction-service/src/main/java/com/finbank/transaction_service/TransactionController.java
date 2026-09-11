package com.finbank.transaction_service;

import com.finbank.transaction_service.client.AccountServiceClient;
import com.finbank.transaction_service.client.CustomerServiceClient;
import com.finbank.transaction_service.dto.AmountDto;
import com.finbank.transaction_service.dto.TransferRequest;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private AccountServiceClient accountServiceClient;
    
    @Autowired
    private CustomerServiceClient customerServiceClient;
    
    // In-memory transactions
    private static final Map<String, Map<String, Object>> transactions = new HashMap<>();

    @GetMapping
    public String getTransactions() {
        return "List of transactions";
    }

    @PostMapping
    public String createTransaction() {
        return "Transaction created";
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequest request) {
        try {
            // Bước 2: Kiểm tra tài khoản nguồn
            try {
                accountServiceClient.getAccount(request.getFromAccountNumber());
            } catch (FeignException.NotFound e) {
                return ResponseEntity.badRequest().body("FAILED: Tài khoản nguồn không tồn tại");
            }
            
            // Bước 3: Kiểm tra tài khoản đích
            try {
                accountServiceClient.getAccount(request.getToAccountNumber());
            } catch (FeignException.NotFound e) {
                return ResponseEntity.badRequest().body("FAILED: Tài khoản đích không tồn tại");
            }
            
            // Bước 4: Kiểm tra số dư và trừ/cộng tiền
            try {
                accountServiceClient.debit(request.getFromAccountNumber(), new AmountDto(request.getAmount()));
                accountServiceClient.credit(request.getToAccountNumber(), new AmountDto(request.getAmount()));
            } catch (FeignException.BadRequest e) {
                return ResponseEntity.badRequest().body("FAILED: Không đủ số dư");
            }
            
            // Bước 5: Lưu giao dịch
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
    
    @GetMapping("/{id}/detail")
    public ResponseEntity<Map<String, Object>> getTransactionDetail(@PathVariable String id) {
        if (!transactions.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> tx = transactions.get(id);
        Map<String, Object> response = new HashMap<>(tx);
        
        try {
            // Giả định ID của user là cùng mã với account number
            String customerId = (String) tx.get("from"); 
            Map<String, Object> customer = customerServiceClient.getCustomer(customerId).getBody();
            response.put("customerInfo", customer);
        } catch (Exception e) {
            response.put("customerInfo", "Không thể lấy thông tin khách hàng");
        }
        
        return ResponseEntity.ok(response);
    }
}
