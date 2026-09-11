package com.finbank.transaction_service.dto;

public class AmountDto {
    private Long amount;
    
    public AmountDto() {}
    public AmountDto(Long amount) { this.amount = amount; }
    
    public Long getAmount() { return amount; }
    public void setAmount(Long amount) { this.amount = amount; }
}
