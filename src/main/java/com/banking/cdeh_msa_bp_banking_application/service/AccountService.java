package com.banking.cdeh_msa_bp_banking_application.service;

import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountService {
    Mono<AccountResponseDto> createAccount(AccountRequestDto accountRequestDto);
    Mono<AccountResponseDto> getAccountById(UUID accountId);
    Mono<AccountResponseDto> getAccountByNumber(String accountNumber);
    Flux<AccountResponseDto> getAllAccounts();
    Flux<AccountResponseDto> getAccountsByCustomerId(UUID customerId);
    Mono<AccountResponseDto> updateAccount(UUID accountId, AccountRequestDto accountRequestDto);
    Mono<AccountResponseDto> updateAccountBalance(UUID accountId, BigDecimal balance);
    Mono<Void> deleteAccount(UUID accountId);
}
