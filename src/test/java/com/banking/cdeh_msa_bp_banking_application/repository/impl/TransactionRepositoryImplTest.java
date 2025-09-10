package com.banking.cdeh_msa_bp_banking_application.repository.impl;

import com.banking.cdeh_msa_bp_banking_application.configuration.ApplicationProperties;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionCreateRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.TransactionUpdateRequestDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionRepositoryImplTest {

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private ApplicationProperties.Url url;

    @Mock
    private ApplicationProperties.ServiceConfig transactionService;

    @InjectMocks
    private TransactionRepositoryImpl transactionRepository;

    private MockWebServer mockWebServer;
    private WebClient webClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        transactionRepository = new TransactionRepositoryImpl(webClient, applicationProperties);

        when(applicationProperties.getUrl()).thenReturn(url);
        when(url.getTransactionService()).thenReturn(transactionService);
        when(transactionService.getFullUrl()).thenReturn(mockWebServer.url("/transactions").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void createTransaction_ShouldReturnTransactionResponseDto() {
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

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "transactionId": "%s",
                        "customerId": "%s",
                        "sourceAccount": "ACC123456",
                        "amount": 100.00,
                        "initialBalance": 500.00,
                        "availableBalance": 400.00,
                        "createdAt": "2024-01-01T10:00:00",
                        "updatedAt": "2024-01-01T10:00:00"
                    }
                    """.formatted(transactionId, customerId)));

        // When & Then
        StepVerifier.create(transactionRepository.createTransaction(request))
                .expectNextMatches(response ->
                    response.getTransactionId().equals(transactionId) &&
                    response.getCustomerId().equals(customerId) &&
                    response.getAmount().equals(new BigDecimal("100.00")))
                .verifyComplete();
    }

    @Test
    void getTransactionById_ShouldReturnTransactionResponseDto() {
        // Given
        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "transactionId": "%s",
                        "customerId": "%s",
                        "sourceAccount": "ACC123456",
                        "amount": 100.00,
                        "initialBalance": 500.00,
                        "availableBalance": 400.00,
                        "createdAt": "2024-01-01T10:00:00",
                        "updatedAt": "2024-01-01T10:00:00"
                    }
                    """.formatted(transactionId, customerId)));

        // When & Then
        StepVerifier.create(transactionRepository.getTransactionById(transactionId))
                .expectNextMatches(response ->
                    response.getTransactionId().equals(transactionId) &&
                    response.getAmount().equals(new BigDecimal("100.00")))
                .verifyComplete();
    }

    @Test
    void updateTransaction_ShouldReturnUpdatedTransaction() {
        // Given
        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        TransactionUpdateRequestDto request = TransactionUpdateRequestDto.builder()
                .customerId(customerId)
                .sourceAccount("ACC123456")
                .amount(new BigDecimal("150.00"))
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "transactionId": "%s",
                        "customerId": "%s",
                        "sourceAccount": "ACC123456",
                        "amount": 150.00,
                        "initialBalance": 500.00,
                        "availableBalance": 350.00,
                        "createdAt": "2024-01-01T10:00:00",
                        "updatedAt": "2024-01-01T11:00:00"
                    }
                    """.formatted(transactionId, customerId)));

        // When & Then
        StepVerifier.create(transactionRepository.updateTransaction(transactionId, request))
                .expectNextMatches(response ->
                    response.getAmount().equals(new BigDecimal("150.00")))
                .verifyComplete();
    }

    @Test
    void deleteTransaction_ShouldReturnVoid() {
        // Given
        UUID transactionId = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NO_CONTENT.value()));

        // When & Then
        StepVerifier.create(transactionRepository.deleteTransaction(transactionId))
                .verifyComplete();
    }

    @Test
    void getTransactionsByCustomerIdAndAccountId_ShouldReturnTransactions() {
        // Given
        UUID customerId = UUID.randomUUID();
        String accountNumber = "ACC123456";
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    [
                        {
                            "transactionId": "%s",
                            "customerId": "%s",
                            "sourceAccount": "ACC123456",
                            "amount": 100.00,
                            "initialBalance": 500.00,
                            "availableBalance": 400.00,
                            "createdAt": "2024-01-15T10:00:00",
                            "updatedAt": "2024-01-15T10:00:00"
                        },
                        {
                            "transactionId": "%s",
                            "customerId": "%s",
                            "sourceAccount": "ACC123456",
                            "amount": 50.00,
                            "initialBalance": 400.00,
                            "availableBalance": 350.00,
                            "createdAt": "2024-01-20T14:30:00",
                            "updatedAt": "2024-01-20T14:30:00"
                        }
                    ]
                    """.formatted(UUID.randomUUID(), customerId, UUID.randomUUID(), customerId)));

        // When & Then
        StepVerifier.create(transactionRepository.getTransactionsByCustomerIdAndAccountId(
                customerId, accountNumber, startDate, endDate))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getAllActiveTransactions_ShouldReturnActiveTransactions() {
        // Given
        UUID customerId1 = UUID.randomUUID();
        UUID customerId2 = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    [
                        {
                            "transactionId": "%s",
                            "customerId": "%s",
                            "sourceAccount": "ACC123456",
                            "amount": 100.00,
                            "initialBalance": 500.00,
                            "availableBalance": 400.00,
                            "createdAt": "2024-01-15T10:00:00",
                            "updatedAt": "2024-01-15T10:00:00"
                        },
                        {
                            "transactionId": "%s",
                            "customerId": "%s",
                            "sourceAccount": "ACC789012",
                            "amount": 50.00,
                            "initialBalance": 200.00,
                            "availableBalance": 150.00,
                            "createdAt": "2024-01-16T14:30:00",
                            "updatedAt": "2024-01-16T14:30:00"
                        }
                    ]
                    """.formatted(UUID.randomUUID(), customerId1,
                                  UUID.randomUUID(), customerId2)));

        // When & Then
        StepVerifier.create(transactionRepository.getAllActiveTransactions())
                .expectNextCount(2)
                .verifyComplete();
    }
}
