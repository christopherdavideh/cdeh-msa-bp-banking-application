package com.banking.cdeh_msa_bp_banking_application.service.impl;

import com.banking.cdeh_msa_bp_banking_application.repository.AccountRepository;
import com.banking.cdeh_msa_bp_banking_application.service.AccountService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountResponseDto;
import com.banking.cdeh_msa_bp_banking_application.util.LogMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Mono<AccountResponseDto> createAccount(AccountRequestDto accountRequestDto) {
        return validateAccountRequest(accountRequestDto)
                .then(accountRepository.createAccount(accountRequestDto))
                .doFirst(() -> log.info(LogMessages.ACCOUNT_CREATE_START, accountRequestDto.getCustomerId()))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_CREATE_SUCCESS, response.getAccountId()))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_CREATE_ERROR, error.getMessage()));
    }

    @Override
    public Mono<AccountResponseDto> getAccountById(UUID accountId) {
        return validateAccountId(accountId)
                .then(accountRepository.getAccountById(accountId))
                .doFirst(() -> log.info(LogMessages.ACCOUNT_GET_BY_ID_START, accountId))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_GET_BY_ID_SUCCESS, response.getAccountNumber()))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_GET_BY_ID_ERROR, accountId, error.getMessage()));
    }

    @Override
    public Mono<AccountResponseDto> getAccountByNumber(String accountNumber) {
        return validateAccountNumber(accountNumber)
                .then(accountRepository.getAccountByNumber(accountNumber))
                .doFirst(() -> log.info(LogMessages.ACCOUNT_GET_BY_NUMBER_START, accountNumber))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_GET_BY_NUMBER_SUCCESS, response.getAccountId()))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_GET_BY_NUMBER_ERROR, accountNumber, error.getMessage()));
    }

    @Override
    public Flux<AccountResponseDto> getAllAccounts() {
        return accountRepository.getAllAccounts()
                .doFirst(() -> log.info(LogMessages.ACCOUNT_GET_ALL_START))
                .doOnComplete(() -> log.info(LogMessages.ACCOUNT_GET_ALL_SUCCESS))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_GET_ALL_ERROR, error.getMessage()));
    }

    @Override
    public Flux<AccountResponseDto> getAccountsByCustomerId(UUID customerId) {
        return validateCustomerId(customerId)
                .thenMany(accountRepository.getAccountsByCustomerId(customerId))
                .doFirst(() -> log.info(LogMessages.ACCOUNT_GET_BY_CUSTOMER_START, customerId))
                .doOnComplete(() -> log.info(LogMessages.ACCOUNT_GET_BY_CUSTOMER_SUCCESS, customerId))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_GET_BY_CUSTOMER_ERROR, customerId, error.getMessage()));
    }

    @Override
    public Mono<AccountResponseDto> updateAccount(UUID accountId, AccountRequestDto accountRequestDto) {
        return validateAccountId(accountId)
                .then(validateAccountRequest(accountRequestDto))
                .then(accountRepository.updateAccount(accountId, accountRequestDto))
                .doFirst(() -> log.info(LogMessages.ACCOUNT_UPDATE_START, accountId))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_UPDATE_SUCCESS, response.getAccountId()))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_UPDATE_ERROR, accountId, error.getMessage()));
    }

    @Override
    public Mono<AccountResponseDto> updateAccountBalance(UUID accountId, BigDecimal balance) {
        return validateAccountId(accountId)
                .then(validateBalance(balance))
                .then(accountRepository.updateAccountBalance(accountId, balance))
                .doFirst(() -> log.info(LogMessages.ACCOUNT_UPDATE_BALANCE_START, accountId, balance))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_UPDATE_BALANCE_SUCCESS, response.getAccountId()))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_UPDATE_BALANCE_ERROR, accountId, error.getMessage()));
    }

    @Override
    public Mono<Void> deleteAccount(UUID accountId) {
        return validateAccountId(accountId)
                .then(accountRepository.deleteAccount(accountId))
                .doFirst(() -> log.info(LogMessages.ACCOUNT_DELETE_START, accountId))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_DELETE_SUCCESS, accountId))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_DELETE_ERROR, accountId, error.getMessage()));
    }

    private Mono<Void> validateAccountId(UUID accountId) {
        return Mono.fromRunnable(() -> {
            if (accountId == null) {
                throw new IllegalArgumentException("Account ID cannot be null");
            }
        });
    }

    private Mono<Void> validateCustomerId(UUID customerId) {
        return Mono.fromRunnable(() -> {
            if (customerId == null) {
                throw new IllegalArgumentException("Customer ID cannot be null");
            }
        });
    }

    private Mono<Void> validateAccountNumber(String accountNumber) {
        return Mono.fromRunnable(() -> {
            if (accountNumber == null || accountNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Account number cannot be null or empty");
            }
        });
    }

    private Mono<Void> validateBalance(BigDecimal balance) {
        return Mono.fromRunnable(() -> {
            if (balance == null) {
                throw new IllegalArgumentException("Balance cannot be null");
            }
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Balance cannot be negative");
            }
        });
    }

    private Mono<Void> validateAccountRequest(AccountRequestDto accountRequestDto) {
        return Mono.fromRunnable(() -> {
            if (accountRequestDto == null) {
                throw new IllegalArgumentException("Account request cannot be null");
            }
            if (accountRequestDto.getCustomerId() == null) {
                throw new IllegalArgumentException("Customer ID is required");
            }
            if (accountRequestDto.getAccountType() == null || accountRequestDto.getAccountType().trim().isEmpty()) {
                throw new IllegalArgumentException("Account type is required");
            }
        });
    }
}
