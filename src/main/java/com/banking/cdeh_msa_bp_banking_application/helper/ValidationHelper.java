package com.banking.cdeh_msa_bp_banking_application.helper;

import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionCreateRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionUpdateRequestDto;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ValidationHelper {
    public static Mono<Void> validateId(UUID id, String fieldName) {
        return Mono.fromRunnable(() -> {
            if (id == null) {
                throw new IllegalArgumentException(fieldName + " cannot be null");
            }
        });
    }

    public static Mono<Void> validateStringNotEmpty(String value, String fieldName) {
        return Mono.fromRunnable(() -> {
            if (value == null || value.trim().isEmpty()) {
                throw new IllegalArgumentException(fieldName + " cannot be null or empty");
            }
        });
    }

    public static Mono<Void> validateAmount(BigDecimal amount) {
        return Mono.fromRunnable(() -> {
            if (amount == null) {
                throw new IllegalArgumentException("Amount cannot be null");
            }
        });
    }

    public static Mono<Void> validateAmountNotNegative(BigDecimal amount) {
        return Mono.fromRunnable(() -> {
            if (amount == null) {
                throw new IllegalArgumentException("Amount cannot be null");
            }
            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Amount cannot be negative");
            }
        });
    }

    public static Mono<Void> validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
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

    public static Mono<Void> validateNotNull(Object object, String fieldName) {
        return Mono.fromRunnable(() -> {
            if (object == null) {
                throw new IllegalArgumentException(fieldName + " cannot be null");
            }
        });
    }

    public static Mono<Void> validateTransactionCreateRequest(TransactionCreateRequestDto request) {
        return Mono.fromRunnable(() -> {
            if (request == null) {
                throw new IllegalArgumentException("Transaction create request cannot be null");
            }
            if (request.getCustomerId() == null) {
                throw new IllegalArgumentException("Customer ID is required");
            }
            if (request.getSourceAccount() == null || request.getSourceAccount().trim().isEmpty()) {
                throw new IllegalArgumentException("Source account is required");
            }
            if (request.getAmount() == null) {
                throw new IllegalArgumentException("Amount is required");
            }
        });
    }

    public static Mono<Void> validateTransactionUpdateRequest(TransactionUpdateRequestDto request) {
        return Mono.fromRunnable(() -> {
            if (request == null) {
                throw new IllegalArgumentException("Transaction update request cannot be null");
            }
            if (request.getCustomerId() == null) {
                throw new IllegalArgumentException("Customer ID is required");
            }
            if (request.getSourceAccount() == null || request.getSourceAccount().trim().isEmpty()) {
                throw new IllegalArgumentException("Source account is required");
            }
            if (request.getAmount() == null) {
                throw new IllegalArgumentException("Amount is required");
            }
        });
    }

    public static Mono<Void> validateTransactionId(UUID transactionId) {
        return validateId(transactionId, "Transaction ID");
    }

    public static Mono<Void> validateCustomerId(UUID customerId) {
        return validateId(customerId, "Customer ID");
    }

    public static Mono<Void> validateAccountId(UUID accountId) {
        return validateId(accountId, "Account ID");
    }

    public static Mono<Void> validateAccountNumber(String accountNumber) {
        return validateStringNotEmpty(accountNumber, "Account number");
    }


    public static Mono<Void> validateAccountRequest(AccountRequestDto accountRequestDto) {
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
