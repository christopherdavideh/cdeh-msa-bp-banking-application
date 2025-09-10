package com.banking.cdeh_msa_bp_banking_application.repository.impl;

import com.banking.cdeh_msa_bp_banking_application.configuration.ApplicationProperties;
import com.banking.cdeh_msa_bp_banking_application.repository.TransactionRepository;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionCreateRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final WebClient webClient;
    private final ApplicationProperties applicationProperties;

    @Override
    public Mono<TransactionResponseDto> createTransaction(TransactionCreateRequestDto transactionCreateRequestDto) {
        return webClient.post()
                .uri(applicationProperties.getUrl().getTransactionService().getFullUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionCreateRequestDto)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class);
    }

    @Override
    public Mono<TransactionResponseDto> getTransactionById(UUID transactionId) {
        return webClient.get()
                .uri(applicationProperties.getUrl().getTransactionService().getFullUrl() + "/{transactionId}", transactionId)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class);
    }

    @Override
    public Mono<TransactionResponseDto> updateTransaction(UUID transactionId, TransactionUpdateRequestDto transactionUpdateRequestDto) {
        return webClient.put()
                .uri(applicationProperties.getUrl().getTransactionService().getFullUrl() + "/{transactionId}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionUpdateRequestDto)
                .retrieve()
                .bodyToMono(TransactionResponseDto.class);
    }

    @Override
    public Mono<Void> deleteTransaction(UUID transactionId) {
        return webClient.delete()
                .uri(applicationProperties.getUrl().getTransactionService().getFullUrl() + "/{transactionId}", transactionId)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Flux<TransactionResponseDto> getTransactionsByCustomerIdAndAccountId(UUID customerId, String accountNumber, LocalDateTime startDate, LocalDateTime endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String startDateStr = startDate.format(formatter);
        String endDateStr = endDate.format(formatter);
        String uriTemplate = applicationProperties.getUrl().getTransactionService().getFullUrl()
                + "/customer/{customerId}/account/{accountNumber}";

        return webClient.get()
                .uri(uriTemplate + "?startDate={startDate}&endDate={endDate}",
                        customerId, accountNumber, startDateStr, endDateStr)
                .retrieve()
                .bodyToFlux(TransactionResponseDto.class);
    }

    @Override
    public Flux<TransactionResponseDto> getAllActiveTransactions() {
        return webClient.get()
                .uri(applicationProperties.getUrl().getTransactionService().getFullUrl())
                .retrieve()
                .bodyToFlux(TransactionResponseDto.class);
    }
}