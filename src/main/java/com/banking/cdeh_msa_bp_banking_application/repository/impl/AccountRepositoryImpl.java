package com.banking.cdeh_msa_bp_banking_application.repository.impl;

import com.banking.cdeh_msa_bp_banking_application.configuration.ApplicationProperties;
import com.banking.cdeh_msa_bp_banking_application.repository.AccountRepository;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final WebClient webClient;
    private final ApplicationProperties applicationProperties;

    @Override
    public Mono<AccountResponseDto> createAccount(AccountRequestDto accountRequestDto) {
        return webClient.post()
                .uri(applicationProperties.getUrl().getAccountService().getFullUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(accountRequestDto)
                .retrieve()
                .bodyToMono(AccountResponseDto.class);
    }

    @Override
    public Mono<AccountResponseDto> getAccountById(UUID accountId) {
        return webClient.get()
                .uri(applicationProperties.getUrl().getAccountService().getFullUrl() + "/{accountId}", accountId)
                .retrieve()
                .bodyToMono(AccountResponseDto.class);
    }

    @Override
    public Mono<AccountResponseDto> getAccountByNumber(String accountNumber) {
        return webClient.get()
                .uri(applicationProperties.getUrl().getAccountService().getFullUrl() + "/numero/{accountNumber}", accountNumber)
                .retrieve()
                .bodyToMono(AccountResponseDto.class);
    }

    @Override
    public Flux<AccountResponseDto> getAllAccounts() {
        return webClient.get()
                .uri(applicationProperties.getUrl().getAccountService().getFullUrl())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccountResponseDto>>() {})
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Flux<AccountResponseDto> getAccountsByCustomerId(UUID customerId) {
        return webClient.get()
                .uri(applicationProperties.getUrl().getAccountService().getFullUrl() + "/cliente/{customerId}", customerId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<AccountResponseDto>>() {})
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<AccountResponseDto> updateAccount(UUID accountId, AccountRequestDto accountRequestDto) {
        return webClient.put()
                .uri(applicationProperties.getUrl().getAccountService().getFullUrl() + "/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(accountRequestDto)
                .retrieve()
                .bodyToMono(AccountResponseDto.class);
    }

    @Override
    public Mono<AccountResponseDto> updateAccountBalance(UUID accountId, BigDecimal balance) {
        Map<String, BigDecimal> balanceUpdate = Map.of("initialBalance", balance);

        return webClient.patch()
                .uri(applicationProperties.getUrl().getAccountService().getFullUrl() + "/{accountId}/balance", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(balanceUpdate)
                .retrieve()
                .bodyToMono(AccountResponseDto.class);
    }

    @Override
    public Mono<Void> deleteAccount(UUID accountId) {
        return webClient.delete()
                .uri(applicationProperties.getUrl().getAccountService().getFullUrl() + "/{accountId}", accountId)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
