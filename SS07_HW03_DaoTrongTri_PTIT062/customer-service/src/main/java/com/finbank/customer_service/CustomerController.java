package com.finbank.customer_service;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    
    @GetMapping("/{id}")
    public Map<String, Object> getCustomer(@PathVariable("id") String id) {
        Map<String, Object> customer = new HashMap<>();
        customer.put("id", id);
        customer.put("name", "Nguyen Van " + id);
        customer.put("email", "user" + id + "@finbank.com");
        return customer;
    }
    
    @GetMapping
    public String getAllCustomers() {
        return "List of customers";
    }
    
    @PostMapping
    public String createCustomer() {
        return "Customer created";
    }
}
