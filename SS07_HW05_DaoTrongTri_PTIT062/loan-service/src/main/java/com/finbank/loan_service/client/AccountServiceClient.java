package com.finbank.loan_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Map;

@FeignClient(name = "account-service")
public interface AccountServiceClient {
    @GetMapping("/api/accounts/customer/{customerId}")
    ResponseEntity<List<Map<String, Object>>> getAccountsByCustomer(@PathVariable("customerId") String customerId);
}
