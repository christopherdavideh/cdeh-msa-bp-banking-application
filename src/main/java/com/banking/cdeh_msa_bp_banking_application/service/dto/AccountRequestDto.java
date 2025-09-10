package com.banking.cdeh_msa_bp_banking_application.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDto {
    private String accountNumber;
    private String accountType;
    private BigDecimal initialBalance;
    private Boolean accountStatus;
    private UUID customerId;
}
