package com.banking.cdeh_msa_bp_banking_application.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartyDto {
    private String name;
    private String gender;
    private Integer age;
    private String identification;
    private String address;
    private String phone;
}
