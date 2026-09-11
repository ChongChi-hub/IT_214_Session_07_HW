package com.finbank.loan_service;

import com.finbank.loan_service.client.AccountServiceClient;
import com.finbank.loan_service.client.CustomerServiceClient;
import com.finbank.loan_service.dto.LoanRequest;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private CustomerServiceClient customerServiceClient;

    @Autowired
    private AccountServiceClient accountServiceClient;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLoan(@RequestBody LoanRequest request) {
        try {
            // Bước 2: Kiểm tra customer
            Map<String, Object> customer;
            try {
                customer = customerServiceClient.getCustomer(request.getCustomerId()).getBody();
            } catch (FeignException.NotFound e) {
                return ResponseEntity.badRequest().body("Lỗi: Khách hàng không tồn tại");
            }
            
            // Bước 3: Lấy danh sách tài khoản
            List<Map<String, Object>> accounts = accountServiceClient.getAccountsByCustomer(request.getCustomerId()).getBody();
            if (accounts == null || accounts.isEmpty()) {
                return ResponseEntity.badRequest().body("Lỗi: Khách hàng không có tài khoản active (no active account)");
            }
            
            // Lấy tài khoản đầu tiên làm tài khoản nhận giải ngân
            String disbursementAccount = (String) accounts.get(0).get("accountNumber");
            
            // Bước 4: Tính lãi dự kiến (8%/năm)
            double annualInterestRate = 0.08;
            double monthlyInterestRate = annualInterestRate / 12;
            double expectedMonthlyPayment = request.getAmount() * monthlyInterestRate + (double) request.getAmount() / request.getTermMonths();
            
            // Bước 5: Response
            Map<String, Object> loanResponse = new HashMap<>();
            loanResponse.put("loanId", UUID.randomUUID().toString());
            loanResponse.put("status", "PENDING");
            loanResponse.put("amount", request.getAmount());
            loanResponse.put("termMonths", request.getTermMonths());
            loanResponse.put("purpose", request.getPurpose());
            loanResponse.put("monthlyPayment", Math.round(expectedMonthlyPayment));
            loanResponse.put("disbursementAccount", disbursementAccount);
            loanResponse.put("customerInfo", customer);
            
            return ResponseEntity.ok(loanResponse);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống: " + e.getMessage());
        }
    }
}
