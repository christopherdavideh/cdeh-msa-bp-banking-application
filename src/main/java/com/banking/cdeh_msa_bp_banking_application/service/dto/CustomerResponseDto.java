package com.banking.cdeh_msa_bp_banking_application.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {
    private UUID customerId;
    private PartyDto party;
    private String password;
    private Boolean status;
}
