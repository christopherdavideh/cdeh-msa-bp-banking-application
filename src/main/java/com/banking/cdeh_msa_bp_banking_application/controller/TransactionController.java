package com.banking.cdeh_msa_bp_banking_application.controller;

import com.banking.cdeh_msa_bp_banking_application.service.TransactionService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionCreateRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionController {

    TransactionService transactionService;

    @PostMapping
    public Mono<ResponseEntity<TransactionResponseDto>> createTransaction(
            @Valid @RequestBody TransactionCreateRequestDto transactionCreateRequestDto) {
        return transactionService.createTransaction(transactionCreateRequestDto)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{transactionId}")
    public Mono<ResponseEntity<TransactionResponseDto>> getTransactionById(@PathVariable UUID transactionId) {
        return transactionService.getTransactionById(transactionId)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{transactionId}")
    public Mono<ResponseEntity<TransactionResponseDto>> updateTransaction(
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionUpdateRequestDto transactionUpdateRequestDto) {
        return transactionService.updateTransaction(transactionId, transactionUpdateRequestDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{transactionId}")
    public Mono<ResponseEntity<Void>> deleteTransaction(@PathVariable UUID transactionId) {
        return transactionService.deleteTransaction(transactionId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @GetMapping("/customer/{customerId}/account/{accountNumber}")
    public Mono<ResponseEntity<Flux<TransactionResponseDto>>> getTransactionsByCustomerAndAccount(
            @PathVariable UUID customerId,
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return Mono.just(ResponseEntity.ok(
                transactionService.getTransactionsByCustomerIdAndAccountId(customerId, accountNumber, startDate, endDate)));
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<TransactionResponseDto>>> getAllActiveTransactions() {
        return Mono.just(ResponseEntity.ok(transactionService.getAllActiveTransactions()));
    }
}
