package com.banking.cdeh_msa_bp_banking_application.controller;

import com.banking.cdeh_msa_bp_banking_application.service.TransactionService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionCreateRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionUpdateRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private TransactionService transactionService;

    @Test
    void createTransaction_WithValidData_ShouldReturnCreatedTransaction() {
        // Given
        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        TransactionCreateRequestDto request = TransactionCreateRequestDto.builder()
                .customerId(customerId)
                .sourceAccount("ACC123456")
                .amount(new BigDecimal("100.00"))
                .initialBalance(new BigDecimal("500.00"))
                .availableBalance(new BigDecimal("400.00"))
                .build();

        TransactionResponseDto expectedResponse = TransactionResponseDto.builder()
                .transactionId(transactionId)
                .customerId(customerId)
                .sourceAccount("ACC123456")
                .amount(new BigDecimal("100.00"))
                .initialBalance(new BigDecimal("500.00"))
                .availableBalance(new BigDecimal("400.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .customerName("John Doe")
                .accountType("SAVINGS")
                .build();

        when(transactionService.createTransaction(any(TransactionCreateRequestDto.class)))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionResponseDto.class)
                .value(response -> {
                    assert response.getTransactionId().equals(transactionId);
                    assert response.getCustomerId().equals(customerId);
                    assert response.getAmount().equals(new BigDecimal("100.00"));
                });

        verify(transactionService).createTransaction(any(TransactionCreateRequestDto.class));
    }

    @Test
    void getTransactionById_WithValidId_ShouldReturnTransaction() {
        // Given
        UUID transactionId = UUID.randomUUID();
        TransactionResponseDto expectedResponse = TransactionResponseDto.builder()
                .transactionId(transactionId)
                .customerId(UUID.randomUUID())
                .sourceAccount("ACC123456")
                .amount(new BigDecimal("100.00"))
                .customerName("John Doe")
                .accountType("SAVINGS")
                .build();

        when(transactionService.getTransactionById(transactionId))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.get()
                .uri("/api/movimientos/{transactionId}", transactionId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDto.class)
                .value(response -> {
                    assert response.getTransactionId().equals(transactionId);
                    assert response.getAmount().equals(new BigDecimal("100.00"));
                });

        verify(transactionService).getTransactionById(transactionId);
    }

    @Test
    void updateTransaction_WithValidData_ShouldReturnUpdatedTransaction() {
        // Given
        UUID transactionId = UUID.randomUUID();
        TransactionUpdateRequestDto request = TransactionUpdateRequestDto.builder()
                .amount(new BigDecimal("150.00"))
                .build();

        TransactionResponseDto expectedResponse = TransactionResponseDto.builder()
                .transactionId(transactionId)
                .customerId(UUID.randomUUID())
                .sourceAccount("ACC123456")
                .amount(new BigDecimal("150.00"))
                .customerName("John Doe")
                .accountType("SAVINGS")
                .build();

        when(transactionService.updateTransaction(eq(transactionId), any(TransactionUpdateRequestDto.class)))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.put()
                .uri("/api/movimientos/{transactionId}", transactionId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionResponseDto.class)
                .value(response -> {
                    assert response.getTransactionId().equals(transactionId);
                    assert response.getAmount().equals(new BigDecimal("150.00"));
                });

        verify(transactionService).updateTransaction(eq(transactionId), any(TransactionUpdateRequestDto.class));
    }

    @Test
    void deleteTransaction_WithValidId_ShouldReturnNoContent() {
        // Given
        UUID transactionId = UUID.randomUUID();

        when(transactionService.deleteTransaction(transactionId))
                .thenReturn(Mono.empty());

        // When & Then
        webTestClient.delete()
                .uri("/api/movimientos/{transactionId}", transactionId)
                .exchange()
                .expectStatus().isNoContent();

        verify(transactionService).deleteTransaction(transactionId);
    }

    @Test
    void getTransactionsByCustomerAndAccount_WithValidData_ShouldReturnTransactions() {
        // Given
        UUID customerId = UUID.randomUUID();
        String accountNumber = "ACC123456";
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);

        TransactionResponseDto transaction1 = TransactionResponseDto.builder()
                .transactionId(UUID.randomUUID())
                .customerId(customerId)
                .sourceAccount(accountNumber)
                .amount(new BigDecimal("100.00"))
                .createdAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .updatedAt(LocalDateTime.of(2024, 1, 15, 10, 0))
                .customerName("John Doe")
                .accountType("SAVINGS")
                .build();

        when(transactionService.getTransactionsByCustomerIdAndAccountId(
                customerId, accountNumber, startDate, endDate))
                .thenReturn(Flux.just(transaction1));

        // When & Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/movimientos/cliente/{customerId}/numero/{accountNumber}")
                        .queryParam("startDate", "2024-01-01T00:00:00")
                        .queryParam("endDate", "2024-01-31T23:59:00")
                        .build(customerId, accountNumber))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionResponseDto.class)
                .hasSize(1);

        verify(transactionService).getTransactionsByCustomerIdAndAccountId(
                customerId, accountNumber, startDate, endDate);
    }

    @Test
    void getAllActiveTransactions_ShouldReturnActiveTransactions() {
        // Given
        TransactionResponseDto transaction1 = TransactionResponseDto.builder()
                .transactionId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .sourceAccount("ACC123456")
                .amount(new BigDecimal("100.00"))
                .customerName("John Doe")
                .accountType("SAVINGS")
                .build();

        TransactionResponseDto transaction2 = TransactionResponseDto.builder()
                .transactionId(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .sourceAccount("ACC789012")
                .amount(new BigDecimal("200.00"))
                .customerName("Jane Smith")
                .accountType("CHECKING")
                .build();

        when(transactionService.getAllActiveTransactions())
                .thenReturn(Flux.just(transaction1, transaction2));

        // When & Then
        webTestClient.get()
                .uri("/api/movimientos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionResponseDto.class)
                .hasSize(2);

        verify(transactionService).getAllActiveTransactions();
    }
}
