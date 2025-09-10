package com.banking.cdeh_msa_bp_banking_application.repository.impl;

import com.banking.cdeh_msa_bp_banking_application.configuration.ApplicationProperties;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.UpdateBalanceDto;
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
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountRepositoryImplTest {

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private ApplicationProperties.Url url;

    @Mock
    private ApplicationProperties.ServiceConfig accountService;

    @InjectMocks
    private AccountRepositoryImpl accountRepository;

    private MockWebServer mockWebServer;
    private WebClient webClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        accountRepository = new AccountRepositoryImpl(webClient, applicationProperties);

        when(applicationProperties.getUrl()).thenReturn(url);
        when(url.getAccountService()).thenReturn(accountService);
        when(accountService.getFullUrl()).thenReturn(mockWebServer.url("/accounts").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void createAccount_ShouldReturnAccountResponseDto() {
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

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "accountId": "%s",
                        "accountNumber": "ACC123456",
                        "accountType": "SAVINGS",
                        "initialBalance": 1000.00,
                        "accountStatus": true,
                        "customerId": "%s"
                    }
                    """.formatted(accountId, customerId)));

        // When & Then
        StepVerifier.create(accountRepository.createAccount(request))
                .expectNextMatches(response ->
                    response.getAccountId().equals(accountId) &&
                    response.getAccountNumber().equals("ACC123456") &&
                    response.getAccountType().equals("SAVINGS") &&
                    response.getCustomerId().equals(customerId))
                .verifyComplete();
    }

    @Test
    void getAccountById_ShouldReturnAccountResponseDto() {
        // Given
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "accountId": "%s",
                        "accountNumber": "ACC123456",
                        "accountType": "SAVINGS",
                        "initialBalance": 1000.00,
                        "accountStatus": true,
                        "customerId": "%s"
                    }
                    """.formatted(accountId, customerId)));

        // When & Then
        StepVerifier.create(accountRepository.getAccountById(accountId))
                .expectNextMatches(response ->
                    response.getAccountId().equals(accountId) &&
                    response.getAccountNumber().equals("ACC123456"))
                .verifyComplete();
    }

    @Test
    void getAccountByNumber_ShouldReturnAccountResponseDto() {
        // Given
        String accountNumber = "ACC123456";
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "accountId": "%s",
                        "accountNumber": "ACC123456",
                        "accountType": "SAVINGS",
                        "initialBalance": 1000.00,
                        "accountStatus": true,
                        "customerId": "%s"
                    }
                    """.formatted(accountId, customerId)));

        // When & Then
        StepVerifier.create(accountRepository.getAccountByNumber(accountNumber))
                .expectNextMatches(response ->
                    response.getAccountNumber().equals(accountNumber) &&
                    response.getAccountType().equals("SAVINGS"))
                .verifyComplete();
    }

    @Test
    void getAllAccounts_ShouldReturnFluxOfAccounts() {
        // Given
        UUID accountId1 = UUID.randomUUID();
        UUID accountId2 = UUID.randomUUID();
        UUID customerId1 = UUID.randomUUID();
        UUID customerId2 = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    [
                        {
                            "accountId": "%s",
                            "accountNumber": "ACC123456",
                            "accountType": "SAVINGS",
                            "initialBalance": 1000.00,
                            "accountStatus": true,
                            "customerId": "%s"
                        },
                        {
                            "accountId": "%s",
                            "accountNumber": "ACC789012",
                            "accountType": "CHECKING",
                            "initialBalance": 2000.00,
                            "accountStatus": true,
                            "customerId": "%s"
                        }
                    ]
                    """.formatted(accountId1, customerId1, accountId2, customerId2)));

        // When & Then
        StepVerifier.create(accountRepository.getAllAccounts())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getAccountsByCustomerId_ShouldReturnFluxOfAccounts() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID accountId1 = UUID.randomUUID();
        UUID accountId2 = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    [
                        {
                            "accountId": "%s",
                            "accountNumber": "ACC123456",
                            "accountType": "SAVINGS",
                            "initialBalance": 1000.00,
                            "accountStatus": true,
                            "customerId": "%s"
                        },
                        {
                            "accountId": "%s",
                            "accountNumber": "ACC789012",
                            "accountType": "CHECKING",
                            "initialBalance": 2000.00,
                            "accountStatus": true,
                            "customerId": "%s"
                        }
                    ]
                    """.formatted(accountId1, customerId, accountId2, customerId)));

        // When & Then
        StepVerifier.create(accountRepository.getAccountsByCustomerId(customerId))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void updateAccount_ShouldReturnUpdatedAccount() {
        // Given
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        AccountRequestDto request = AccountRequestDto.builder()
                .accountNumber("ACC123456")
                .accountType("CHECKING")
                .initialBalance(new BigDecimal("2000.00"))
                .accountStatus(true)
                .customerId(customerId)
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "accountId": "%s",
                        "accountNumber": "ACC123456",
                        "accountType": "CHECKING",
                        "initialBalance": 2000.00,
                        "accountStatus": true,
                        "customerId": "%s"
                    }
                    """.formatted(accountId, customerId)));

        // When & Then
        StepVerifier.create(accountRepository.updateAccount(accountId, request))
                .expectNextMatches(response ->
                    response.getAccountType().equals("CHECKING") &&
                    response.getInitialBalance().equals(new BigDecimal("2000.00")))
                .verifyComplete();
    }

    @Test
    void updateAccountBalance_ShouldReturnUpdatedAccount() {
        // Given
        String accountNumber = "ACC123456";
        BigDecimal newBalance = new BigDecimal("1500.00");
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "accountId": "%s",
                        "accountNumber": "ACC123456",
                        "accountType": "SAVINGS",
                        "initialBalance": 1500.00,
                        "accountStatus": true,
                        "customerId": "%s"
                    }
                    """.formatted(accountId, customerId)));

        // When & Then
        StepVerifier.create(accountRepository.updateAccountBalance(accountNumber, newBalance))
                .expectNextMatches(response ->
                    response.getInitialBalance().equals(newBalance) &&
                    response.getAccountNumber().equals(accountNumber))
                .verifyComplete();
    }

    @Test
    void deleteAccount_ShouldReturnVoid() {
        // Given
        UUID accountId = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NO_CONTENT.value()));

        // When & Then
        StepVerifier.create(accountRepository.deleteAccount(accountId))
                .verifyComplete();
    }
}
