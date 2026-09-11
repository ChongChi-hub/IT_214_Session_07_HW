package com.finbank.account_service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired private Environment env;
    @GetMapping("/info")
    public Map<String, String> getInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("port", env.getProperty("local.server.port"));
        info.put("service", "account-service");
        return info;
    }
    @GetMapping
    public String getAllAccounts() { return "List of accounts"; }
    @PostMapping
    public String createAccount() { return "Account created"; }
}
