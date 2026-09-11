package com.finbank.transaction_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "customer-service")
public interface CustomerServiceClient {
    
    @GetMapping("/api/customers/{id}")
    ResponseEntity<Map<String, Object>> getCustomer(@PathVariable("id") String id);
}
