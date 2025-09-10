package com.banking.cdeh_msa_bp_banking_application.controller;

import com.banking.cdeh_msa_bp_banking_application.service.AccountService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.UpdateBalanceDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AccountController.class)
class AccountControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AccountService accountService;

    @Test
    void createAccount_WithValidData_ShouldReturnCreatedAccount() {
        // Given
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        AccountRequestDto request = AccountRequestDto.builder()
                .accountNumber("ACC123456")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("1000.00"))
                .accountStatus(true)
                .customerId(customerId)
                .build();

        AccountResponseDto expectedResponse = AccountResponseDto.builder()
                .accountId(accountId)
                .accountNumber("ACC123456")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("1000.00"))
                .accountStatus(true)
                .customerId(customerId)
                .customerName("John Doe")
                .build();

        when(accountService.createAccount(any(AccountRequestDto.class)))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(AccountResponseDto.class)
                .value(response -> {
                    assert response.getAccountId().equals(accountId);
                    assert response.getAccountNumber().equals("ACC123456");
                    assert response.getAccountType().equals("SAVINGS");
                });

        verify(accountService).createAccount(any(AccountRequestDto.class));
    }

    @Test
    void getAccountById_WithValidId_ShouldReturnAccount() {
        // Given
        UUID accountId = UUID.randomUUID();
        AccountResponseDto expectedResponse = AccountResponseDto.builder()
                .accountId(accountId)
                .accountNumber("ACC123456")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("1000.00"))
                .customerName("John Doe")
                .build();

        when(accountService.getAccountById(accountId))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.get()
                .uri("/api/cuentas/{accountId}", accountId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDto.class)
                .value(response -> {
                    assert response.getAccountId().equals(accountId);
                    assert response.getAccountNumber().equals("ACC123456");
                });

        verify(accountService).getAccountById(accountId);
    }

    @Test
    void getAccountByNumber_WithValidNumber_ShouldReturnAccount() {
        // Given
        String accountNumber = "ACC123456";
        AccountResponseDto expectedResponse = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber(accountNumber)
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("1000.00"))
                .customerName("John Doe")
                .build();

        when(accountService.getAccountByNumber(accountNumber))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.get()
                .uri("/api/cuentas/numero/{accountNumber}", accountNumber)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDto.class)
                .value(response -> {
                    assert response.getAccountNumber().equals(accountNumber);
                    assert response.getAccountType().equals("SAVINGS");
                });

        verify(accountService).getAccountByNumber(accountNumber);
    }

    @Test
    void getAllAccounts_ShouldReturnListOfAccounts() {
        // Given
        AccountResponseDto account1 = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("ACC123456")
                .accountType("SAVINGS")
                .customerName("John Doe")
                .build();

        AccountResponseDto account2 = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("ACC789012")
                .accountType("CHECKING")
                .customerName("Jane Smith")
                .build();

        when(accountService.getAllAccounts())
                .thenReturn(Flux.just(account1, account2));

        // When & Then
        webTestClient.get()
                .uri("/api/cuentas")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AccountResponseDto.class)
                .hasSize(2);

        verify(accountService).getAllAccounts();
    }

    @Test
    void getAccountsByCustomerId_WithValidCustomerId_ShouldReturnAccounts() {
        // Given
        UUID customerId = UUID.randomUUID();
        AccountResponseDto account1 = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("ACC123456")
                .customerId(customerId)
                .customerName("John Doe")
                .build();

        when(accountService.getAccountsByCustomerId(customerId))
                .thenReturn(Flux.just(account1));

        // When & Then
        webTestClient.get()
                .uri("/api/cuentas/cliente/{customerId}", customerId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AccountResponseDto.class)
                .hasSize(1);

        verify(accountService).getAccountsByCustomerId(customerId);
    }

    @Test
    void updateAccount_WithValidData_ShouldReturnUpdatedAccount() {
        // Given
        UUID accountId = UUID.randomUUID();
        AccountRequestDto request = AccountRequestDto.builder()
                .accountNumber("ACC123456")
                .accountType("CHECKING")
                .initialBalance(new BigDecimal("2000.00"))
                .accountStatus(true)
                .customerId(UUID.randomUUID())
                .build();

        AccountResponseDto expectedResponse = AccountResponseDto.builder()
                .accountId(accountId)
                .accountNumber("ACC123456")
                .accountType("CHECKING")
                .initialBalance(new BigDecimal("2000.00"))
                .customerName("John Doe")
                .build();

        when(accountService.updateAccount(eq(accountId), any(AccountRequestDto.class)))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.put()
                .uri("/api/cuentas/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDto.class)
                .value(response -> {
                    assert response.getAccountId().equals(accountId);
                    assert response.getAccountType().equals("CHECKING");
                });

        verify(accountService).updateAccount(eq(accountId), any(AccountRequestDto.class));
    }

    @Test
    void updateAccountBalance_WithValidData_ShouldReturnUpdatedAccount() {
        // Given
        String accountNumber = "ACC123456";
        BigDecimal newBalance = new BigDecimal("1500.00");
        UpdateBalanceDto balanceUpdate = UpdateBalanceDto.builder()
                .initialBalance(newBalance)
                .build();

        AccountResponseDto expectedResponse = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber(accountNumber)
                .initialBalance(newBalance)
                .customerName("John Doe")
                .build();

        when(accountService.updateAccountBalance(accountNumber, newBalance))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.patch()
                .uri("/api/cuentas/{accountNumber}/balance", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(balanceUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AccountResponseDto.class)
                .value(response -> {
                    assert response.getAccountNumber().equals(accountNumber);
                    assert response.getInitialBalance().equals(newBalance);
                });

        verify(accountService).updateAccountBalance(accountNumber, newBalance);
    }

    @Test
    void deleteAccount_WithValidId_ShouldReturnNoContent() {
        // Given
        UUID accountId = UUID.randomUUID();

        when(accountService.deleteAccount(accountId))
                .thenReturn(Mono.empty());

        // When & Then
        webTestClient.delete()
                .uri("/api/cuentas/{accountId}", accountId)
                .exchange()
                .expectStatus().isNoContent();

        verify(accountService).deleteAccount(accountId);
    }
}
