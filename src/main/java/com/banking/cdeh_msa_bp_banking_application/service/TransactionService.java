package com.banking.cdeh_msa_bp_banking_application.service;

import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionCreateRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionUpdateRequestDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

public interface TransactionService {
    Mono<TransactionResponseDto> createTransaction(TransactionCreateRequestDto transactionCreateRequestDto);
    Mono<TransactionResponseDto> getTransactionById(UUID transactionId);
    Mono<TransactionResponseDto> updateTransaction(UUID transactionId, TransactionUpdateRequestDto transactionUpdateRequestDto);
    Mono<Void> deleteTransaction(UUID transactionId);
    Flux<TransactionResponseDto> getTransactionsByCustomerIdAndAccountId(UUID customerId, String accountNumber, LocalDateTime startDate, LocalDateTime endDate);
    Flux<TransactionResponseDto> getAllActiveTransactions();
}
