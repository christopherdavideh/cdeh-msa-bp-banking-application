package com.banking.cdeh_msa_bp_banking_application.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDto {
    private UUID transactionId;
    private UUID customerId;
    private String sourceAccount;
    private BigDecimal initialBalance;
    private BigDecimal amount;
    private BigDecimal availableBalance;
    private Boolean transactionStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
