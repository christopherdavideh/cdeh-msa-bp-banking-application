package com.banking.cdeh_msa_bp_banking_application.controller;

import com.banking.cdeh_msa_bp_banking_application.service.AccountService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.UpdateBalanceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public Mono<ResponseEntity<AccountResponseDto>> createAccount(
            @Valid @RequestBody AccountRequestDto accountRequestDto) {
        return accountService.createAccount(accountRequestDto)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{accountId}")
    public Mono<ResponseEntity<AccountResponseDto>> getAccountById(@PathVariable UUID accountId) {
        return accountService.getAccountById(accountId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/number/{accountNumber}")
    public Mono<ResponseEntity<AccountResponseDto>> getAccountByNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByNumber(accountNumber)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<AccountResponseDto>>> getAllAccounts() {
        return Mono.just(ResponseEntity.ok(accountService.getAllAccounts()));
    }

    @GetMapping("/customer/{customerId}")
    public Mono<ResponseEntity<Flux<AccountResponseDto>>> getAccountsByCustomerId(@PathVariable UUID customerId) {
        return Mono.just(ResponseEntity.ok(accountService.getAccountsByCustomerId(customerId)));
    }

    @PutMapping("/{accountId}")
    public Mono<ResponseEntity<AccountResponseDto>> updateAccount(
            @PathVariable UUID accountId,
            @Valid @RequestBody AccountRequestDto accountRequestDto) {
        return accountService.updateAccount(accountId, accountRequestDto)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/{accountId}/balance")
    public Mono<ResponseEntity<AccountResponseDto>> updateAccountBalance(
            @PathVariable UUID accountId,
            @Valid @RequestBody UpdateBalanceDto balanceUpdate) {
        return accountService.updateAccountBalance(accountId, balanceUpdate.getInitialBalance())
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{accountId}")
    public Mono<ResponseEntity<Void>> deleteAccount(@PathVariable UUID accountId) {
        return accountService.deleteAccount(accountId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}