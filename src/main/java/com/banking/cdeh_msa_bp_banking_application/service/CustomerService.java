package com.banking.cdeh_msa_bp_banking_application.service;

import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerService {
    Mono<CustomerResponseDto> createCustomer(CustomerRequestDto customerRequestDto);
    Mono<CustomerResponseDto> getCustomerById(UUID customerId);
    Flux<CustomerResponseDto> getAllCustomers();
    Mono<CustomerResponseDto> updateCustomer(UUID customerId, CustomerRequestDto customerRequestDto);
    Mono<Void> deleteCustomer(UUID customerId);
}
