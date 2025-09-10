package com.banking.cdeh_msa_bp_banking_application.service.impl;

import com.banking.cdeh_msa_bp_banking_application.repository.TransactionRepository;
import com.banking.cdeh_msa_bp_banking_application.service.TransactionService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionCreateRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionUpdateRequestDto;
import com.banking.cdeh_msa_bp_banking_application.util.LogMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Override
    public Mono<TransactionResponseDto> createTransaction(TransactionCreateRequestDto transactionCreateRequestDto) {
        return validateTransactionCreateRequest(transactionCreateRequestDto)
                .then(transactionRepository.createTransaction(transactionCreateRequestDto))
                .doFirst(() -> log.info(LogMessages.TRANSACTION_CREATE_START,
                        transactionCreateRequestDto.getCustomerId(),
                        transactionCreateRequestDto.getSourceAccount()))
                .doOnSuccess(response -> log.info(LogMessages.TRANSACTION_CREATE_SUCCESS,
                        response.getTransactionId()))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_CREATE_ERROR, error.getMessage()));
    }

    @Override
    public Mono<TransactionResponseDto> getTransactionById(UUID transactionId) {
        return validateTransactionId(transactionId)
                .then(transactionRepository.getTransactionById(transactionId))
                .doFirst(() -> log.info(LogMessages.TRANSACTION_GET_BY_ID_START, transactionId))
                .doOnSuccess(response -> log.info(LogMessages.TRANSACTION_GET_BY_ID_SUCCESS,
                        response.getTransactionId()))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_GET_BY_ID_ERROR, transactionId, error.getMessage()));
    }

    @Override
    public Mono<TransactionResponseDto> updateTransaction(UUID transactionId, TransactionUpdateRequestDto transactionUpdateRequestDto) {
        return validateTransactionId(transactionId)
                .then(validateTransactionUpdateRequest(transactionUpdateRequestDto))
                .then(transactionRepository.updateTransaction(transactionId, transactionUpdateRequestDto))
                .doFirst(() -> log.info(LogMessages.TRANSACTION_UPDATE_START, transactionId))
                .doOnSuccess(response -> log.info(LogMessages.TRANSACTION_UPDATE_SUCCESS,
                        response.getTransactionId()))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_UPDATE_ERROR, transactionId, error.getMessage()));
    }

    @Override
    public Mono<Void> deleteTransaction(UUID transactionId) {
        return validateTransactionId(transactionId)
                .then(transactionRepository.deleteTransaction(transactionId))
                .doFirst(() -> log.info(LogMessages.TRANSACTION_DELETE_START, transactionId))
                .doOnSuccess(response -> log.info(LogMessages.TRANSACTION_DELETE_SUCCESS, transactionId))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_DELETE_ERROR, transactionId, error.getMessage()));
    }

    @Override
    public Flux<TransactionResponseDto> getTransactionsByCustomerIdAndAccountId(UUID customerId, String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        return validateCustomerId(customerId)
                .then(validateAccountNumber(accountNumber))
                .then(validateDateRange(startDate, endDate))
                .thenMany(transactionRepository.getTransactionsByCustomerIdAndAccountId(customerId, accountNumber, startDate, endDate))
                .doFirst(() -> log.info(LogMessages.TRANSACTION_GET_BY_CUSTOMER_ACCOUNT_START,
                        customerId, accountNumber, startDate, endDate))
                .doOnComplete(() -> log.info(LogMessages.TRANSACTION_GET_BY_CUSTOMER_ACCOUNT_SUCCESS,
                        customerId, accountNumber))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_GET_BY_CUSTOMER_ACCOUNT_ERROR,
                        customerId, accountNumber, error.getMessage()));
    }

    @Override
    public Flux<TransactionResponseDto> getAllActiveTransactions() {
        return transactionRepository.getAllActiveTransactions()
                .doFirst(() -> log.info(LogMessages.TRANSACTION_GET_ALL_ACTIVE_START))
                .doOnComplete(() -> log.info(LogMessages.TRANSACTION_GET_ALL_ACTIVE_SUCCESS))
                .doOnError(error -> log.error(LogMessages.TRANSACTION_GET_ALL_ACTIVE_ERROR, error.getMessage()));
    }

    private Mono<Void> validateTransactionId(UUID transactionId) {
        return Mono.fromRunnable(() -> {
            if (transactionId == null) {
                throw new IllegalArgumentException("Transaction ID cannot be null");
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

    private Mono<Void> validateAmount(BigDecimal amount) {
        return Mono.fromRunnable(() -> {
            if (amount == null) {
                throw new IllegalArgumentException("Amount cannot be null");
            }
        });
    }

    private Mono<Void> validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return Mono.fromRunnable(() -> {
            if (startDate == null) {
                throw new IllegalArgumentException("Start date cannot be null");
            }
            if (endDate == null) {
                throw new IllegalArgumentException("End date cannot be null");
            }
            if (startDate.isAfter(endDate)) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
        });
    }

    private Mono<Void> validateTransactionCreateRequest(TransactionCreateRequestDto transactionCreateRequestDto) {
        return Mono.fromRunnable(() -> {
            if (transactionCreateRequestDto == null) {
                throw new IllegalArgumentException("Transaction create request cannot be null");
            }
            if (transactionCreateRequestDto.getCustomerId() == null) {
                throw new IllegalArgumentException("Customer ID is required");
            }
            if (transactionCreateRequestDto.getSourceAccount() == null || transactionCreateRequestDto.getSourceAccount().trim().isEmpty()) {
                throw new IllegalArgumentException("Source account is required");
            }
            if (transactionCreateRequestDto.getAmount() == null) {
                throw new IllegalArgumentException("Amount is required");
            }
        });
    }

    private Mono<Void> validateTransactionUpdateRequest(TransactionUpdateRequestDto transactionUpdateRequestDto) {
        return Mono.fromRunnable(() -> {
            if (transactionUpdateRequestDto == null) {
                throw new IllegalArgumentException("Transaction update request cannot be null");
            }
            if (transactionUpdateRequestDto.getCustomerId() == null) {
                throw new IllegalArgumentException("Customer ID is required");
            }
            if (transactionUpdateRequestDto.getSourceAccount() == null || transactionUpdateRequestDto.getSourceAccount().trim().isEmpty()) {
                throw new IllegalArgumentException("Source account is required");
            }
            if (transactionUpdateRequestDto.getAmount() == null) {
                throw new IllegalArgumentException("Amount is required");
            }
        });
    }
}
