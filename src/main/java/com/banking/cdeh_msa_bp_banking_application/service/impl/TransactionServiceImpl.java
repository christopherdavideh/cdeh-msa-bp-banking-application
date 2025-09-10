package com.banking.cdeh_msa_bp_banking_application.service.impl;

import com.banking.cdeh_msa_bp_banking_application.exception.BadRequestException;
import com.banking.cdeh_msa_bp_banking_application.exception.ResourceNotFoundException;
import com.banking.cdeh_msa_bp_banking_application.helper.ValidationHelper;
import com.banking.cdeh_msa_bp_banking_application.repository.AccountRepository;
import com.banking.cdeh_msa_bp_banking_application.repository.CustomerRepository;
import com.banking.cdeh_msa_bp_banking_application.repository.TransactionRepository;
import com.banking.cdeh_msa_bp_banking_application.service.TransactionService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionCreateRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionUpdateRequestDto;
import com.banking.cdeh_msa_bp_banking_application.util.LogMessages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionServiceImpl implements TransactionService {

    TransactionRepository transactionRepository;
    CustomerRepository customerRepository;
    AccountRepository accountRepository;

    @Override
    public Mono<TransactionResponseDto> createTransaction(TransactionCreateRequestDto transactionCreateRequestDto) {
        return ValidationHelper.validateTransactionCreateRequest(transactionCreateRequestDto)
                .then(transactionRepository.createTransaction(transactionCreateRequestDto))
                .flatMap(this::mapAdditionalData)
                .doFirst(() -> log.info(LogMessages.TRANSACTION_CREATE_START,
                        transactionCreateRequestDto.getCustomerId(),
                        transactionCreateRequestDto.getSourceAccount()))
                .doOnSuccess(response -> log.info(LogMessages.TRANSACTION_CREATE_SUCCESS,
                        response.getTransactionId()))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_CREATE_ERROR, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.BadRequest.class,
                        ex -> Mono.error(new BadRequestException("Invalid transaction data")))
                .onErrorResume(WebClientResponseException.Conflict.class,
                        ex -> Mono.error(new BadRequestException("Transaction already exists")));
    }

    @Override
    public Mono<TransactionResponseDto> getTransactionById(UUID transactionId) {
        return ValidationHelper.validateTransactionId(transactionId)
                .then(transactionRepository.getTransactionById(transactionId))
                .flatMap(this::mapAdditionalData)
                .doFirst(() -> log.info(LogMessages.TRANSACTION_GET_BY_ID_START, transactionId))
                .doOnSuccess(response -> log.info(LogMessages.TRANSACTION_GET_BY_ID_SUCCESS,
                        response.getTransactionId()))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_GET_BY_ID_ERROR, transactionId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Transaction not found with ID: " + transactionId)));
    }

    @Override
    public Mono<TransactionResponseDto> updateTransaction(UUID transactionId, TransactionUpdateRequestDto transactionUpdateRequestDto) {
        return ValidationHelper.validateTransactionId(transactionId)
                .then(ValidationHelper.validateTransactionUpdateRequest(transactionUpdateRequestDto))
                .then(transactionRepository.updateTransaction(transactionId, transactionUpdateRequestDto))
                .flatMap(this::mapAdditionalData)
                .doFirst(() -> log.info(LogMessages.TRANSACTION_UPDATE_START, transactionId))
                .doOnSuccess(response -> log.info(LogMessages.TRANSACTION_UPDATE_SUCCESS,
                        response.getTransactionId()))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_UPDATE_ERROR, transactionId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Transaction not found with ID: " + transactionId)))
                .onErrorResume(WebClientResponseException.BadRequest.class,
                        ex -> Mono.error(new BadRequestException("Invalid transaction data")));
    }

    @Override
    public Mono<Void> deleteTransaction(UUID transactionId) {
        return ValidationHelper.validateTransactionId(transactionId)
                .then(transactionRepository.deleteTransaction(transactionId))
                .doFirst(() -> log.info(LogMessages.TRANSACTION_DELETE_START, transactionId))
                .doOnSuccess(response -> log.info(LogMessages.TRANSACTION_DELETE_SUCCESS, transactionId))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_DELETE_ERROR, transactionId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Transaction not found with ID: " + transactionId)));
    }

    @Override
    public Flux<TransactionResponseDto> getTransactionsByCustomerIdAndAccountId(UUID customerId, String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        return ValidationHelper.validateCustomerId(customerId)
                .then(ValidationHelper.validateAccountNumber(accountNumber))
                .then(ValidationHelper.validateDateRange(startDate, endDate))
                .thenMany(transactionRepository.getTransactionsByCustomerIdAndAccountId(customerId, accountNumber, startDate, endDate))
                .flatMap(this::mapAdditionalData)
                .doFirst(() -> log.info(LogMessages.TRANSACTION_GET_BY_CUSTOMER_ACCOUNT_START,
                        customerId, accountNumber, startDate, endDate))
                .doOnComplete(() -> log.info(LogMessages.TRANSACTION_GET_BY_CUSTOMER_ACCOUNT_SUCCESS,
                        customerId, accountNumber))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_GET_BY_CUSTOMER_ACCOUNT_ERROR,
                        customerId, accountNumber, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Flux.error(new BadRequestException(ex.getMessage())));
    }

    @Override
    public Flux<TransactionResponseDto> getAllActiveTransactions() {
        return transactionRepository.getAllActiveTransactions()
                .flatMap(this::mapAdditionalData)
                .doFirst(() -> log.info(LogMessages.TRANSACTION_GET_ALL_ACTIVE_START))
                .doOnComplete(() -> log.info(LogMessages.TRANSACTION_GET_ALL_ACTIVE_SUCCESS))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_GET_ALL_ACTIVE_ERROR, error.getMessage()));
    }

    private Mono<TransactionResponseDto> mapAdditionalData(TransactionResponseDto transactionResponse) {
        Mono<String> customerNameMono = customerRepository.getCustomerById(transactionResponse.getCustomerId())
                .map(customer -> customer.getParty().getName())
                .onErrorReturn("Unknown Customer");

        Mono<String> accountTypeMono = accountRepository.getAccountByNumber(transactionResponse.getSourceAccount())
                .map(AccountResponseDto::getAccountType)
                .onErrorReturn("Unknown Account Type");

        return Mono.zip(customerNameMono, accountTypeMono)
                .map(tuple -> {
                    transactionResponse.setCustomerName(tuple.getT1());
                    transactionResponse.setAccountType(tuple.getT2());
                    return transactionResponse;
                });
    }
}