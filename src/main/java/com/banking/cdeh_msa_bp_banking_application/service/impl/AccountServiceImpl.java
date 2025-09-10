package com.banking.cdeh_msa_bp_banking_application.service.impl;

import com.banking.cdeh_msa_bp_banking_application.exception.BadRequestException;
import com.banking.cdeh_msa_bp_banking_application.exception.ResourceNotFoundException;
import com.banking.cdeh_msa_bp_banking_application.helper.ValidationHelper;
import com.banking.cdeh_msa_bp_banking_application.repository.AccountRepository;
import com.banking.cdeh_msa_bp_banking_application.repository.CustomerRepository;
import com.banking.cdeh_msa_bp_banking_application.service.AccountService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountResponseDto;
import com.banking.cdeh_msa_bp_banking_application.util.LogMessages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AccountServiceImpl implements AccountService {

    AccountRepository accountRepository;
    CustomerRepository customerRepository;

    @Override
    public Mono<AccountResponseDto> createAccount(AccountRequestDto accountRequestDto) {
        return ValidationHelper.validateAccountRequest(accountRequestDto)
                .then(accountRepository.createAccount(accountRequestDto))
                .flatMap(this::mapCustomerName)
                .doFirst(() -> log.info(LogMessages.ACCOUNT_CREATE_START, accountRequestDto.getCustomerId()))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_CREATE_SUCCESS, response.getAccountId()))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_CREATE_ERROR, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.BadRequest.class,
                        ex -> Mono.error(new BadRequestException("Invalid account data")))
                .onErrorResume(WebClientResponseException.Conflict.class,
                        ex -> Mono.error(new BadRequestException("Account already exists")));
    }

    @Override
    public Mono<AccountResponseDto> getAccountById(UUID accountId) {
        return ValidationHelper.validateAccountId(accountId)
                .then(accountRepository.getAccountById(accountId))
                .flatMap(this::mapCustomerName)
                .doFirst(() -> log.info(LogMessages.ACCOUNT_GET_BY_ID_START, accountId))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_GET_BY_ID_SUCCESS, response.getAccountNumber()))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_GET_BY_ID_ERROR, accountId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Account not found with ID: " + accountId)));
    }

    @Override
    public Mono<AccountResponseDto> getAccountByNumber(String accountNumber) {
        return ValidationHelper.validateAccountNumber(accountNumber)
                .then(accountRepository.getAccountByNumber(accountNumber))
                .flatMap(this::mapCustomerName)
                .doFirst(() -> log.info(LogMessages.ACCOUNT_GET_BY_NUMBER_START, accountNumber))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_GET_BY_NUMBER_SUCCESS, response.getAccountId()))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_GET_BY_NUMBER_ERROR, accountNumber, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Account not found with number: " + accountNumber)));
    }

    @Override
    public Flux<AccountResponseDto> getAllAccounts() {
        return accountRepository.getAllAccounts()
                .flatMap(this::mapCustomerName)
                .doFirst(() -> log.info(LogMessages.ACCOUNT_GET_ALL_START))
                .doOnComplete(() -> log.info(LogMessages.ACCOUNT_GET_ALL_SUCCESS))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_GET_ALL_ERROR, error.getMessage()));
    }

    @Override
    public Flux<AccountResponseDto> getAccountsByCustomerId(UUID customerId) {
        return ValidationHelper.validateCustomerId(customerId)
                .thenMany(accountRepository.getAccountsByCustomerId(customerId))
                .flatMap(this::mapCustomerName)
                .doFirst(() -> log.info(LogMessages.ACCOUNT_GET_BY_CUSTOMER_START, customerId))
                .doOnComplete(() -> log.info(LogMessages.ACCOUNT_GET_BY_CUSTOMER_SUCCESS, customerId))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_GET_BY_CUSTOMER_ERROR, customerId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Flux.error(new BadRequestException(ex.getMessage())));
    }

    @Override
    public Mono<AccountResponseDto> updateAccount(UUID accountId, AccountRequestDto accountRequestDto) {
        return ValidationHelper.validateAccountId(accountId)
                .then(ValidationHelper.validateAccountRequest(accountRequestDto))
                .then(accountRepository.updateAccount(accountId, accountRequestDto))
                .flatMap(this::mapCustomerName)
                .doFirst(() -> log.info(LogMessages.ACCOUNT_UPDATE_START, accountId))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_UPDATE_SUCCESS, response.getAccountId()))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_UPDATE_ERROR, accountId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Account not found with ID: " + accountId)))
                .onErrorResume(WebClientResponseException.BadRequest.class,
                        ex -> Mono.error(new BadRequestException("Invalid account data")));
    }

    @Override
    public Mono<AccountResponseDto> updateAccountBalance(UUID accountId, BigDecimal balance) {
        return ValidationHelper.validateAccountId(accountId)
                .then(ValidationHelper.validateAmountNotNegative(balance))
                .then(accountRepository.updateAccountBalance(accountId, balance))
                .flatMap(this::mapCustomerName)
                .doFirst(() -> log.info(LogMessages.ACCOUNT_UPDATE_BALANCE_START, accountId, balance))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_UPDATE_BALANCE_SUCCESS, response.getAccountId()))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_UPDATE_BALANCE_ERROR, accountId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Account not found with ID: " + accountId)))
                .onErrorResume(WebClientResponseException.BadRequest.class,
                        ex -> Mono.error(new BadRequestException("Invalid balance value")));
    }

    @Override
    public Mono<Void> deleteAccount(UUID accountId) {
        return ValidationHelper.validateAccountId(accountId)
                .then(accountRepository.deleteAccount(accountId))
                .doFirst(() -> log.info(LogMessages.ACCOUNT_DELETE_START, accountId))
                .doOnSuccess(response -> log.info(LogMessages.ACCOUNT_DELETE_SUCCESS, accountId))
                .doOnError(error -> log.error(LogMessages.ACCOUNT_DELETE_ERROR, accountId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Account not found with ID: " + accountId)));
    }

    private Mono<AccountResponseDto> mapCustomerName(AccountResponseDto accountResponse) {
        return customerRepository.getCustomerById(accountResponse.getCustomerId())
                .map(customer -> {
                    accountResponse.setCustomerName(customer.getParty().getName());
                    return accountResponse;
                })
                .onErrorReturn(accountResponse);
    }
}
