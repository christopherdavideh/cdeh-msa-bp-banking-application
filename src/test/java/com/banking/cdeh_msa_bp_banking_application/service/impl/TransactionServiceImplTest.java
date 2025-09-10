package com.banking.cdeh_msa_bp_banking_application.service.impl;

import com.banking.cdeh_msa_bp_banking_application.exception.BadRequestException;
import com.banking.cdeh_msa_bp_banking_application.exception.ResourceNotFoundException;
import com.banking.cdeh_msa_bp_banking_application.repository.AccountRepository;
import com.banking.cdeh_msa_bp_banking_application.repository.CustomerRepository;
import com.banking.cdeh_msa_bp_banking_application.repository.TransactionRepository;
import com.banking.cdeh_msa_bp_banking_application.service.AccountService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void createTransaction_WithValidData_ShouldReturnTransactionResponseDto() {
        // Given
        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String accountNumber = "ACC123456";

        TransactionCreateRequestDto request = TransactionCreateRequestDto.builder()
                .customerId(customerId)
                .sourceAccount(accountNumber)
                .amount(new BigDecimal("100.00"))
                .build();

        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber(accountNumber)
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("500.00"))
                .customerId(customerId)
                .build();

        TransactionResponseDto transactionResponse = TransactionResponseDto.builder()
                .transactionId(transactionId)
                .customerId(customerId)
                .sourceAccount(accountNumber)
                .amount(new BigDecimal("100.00"))
                .initialBalance(new BigDecimal("500.00"))
                .availableBalance(new BigDecimal("400.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CustomerResponseDto customerResponse = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        when(accountService.getAccountByNumber(accountNumber))
                .thenReturn(Mono.just(accountResponse));
        when(transactionRepository.createTransaction(any(TransactionCreateRequestDto.class)))
                .thenReturn(Mono.just(transactionResponse));
        when(accountService.updateAccountBalance(eq(accountNumber), any(BigDecimal.class)))
                .thenReturn(Mono.just(accountResponse));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.just(customerResponse));
        when(accountRepository.getAccountByNumber(accountNumber))
                .thenReturn(Mono.just(accountResponse));

        // When & Then
        StepVerifier.create(transactionService.createTransaction(request))
                .expectNextMatches(response ->
                    response.getTransactionId().equals(transactionId) &&
                    response.getCustomerName().equals("John Doe") &&
                    response.getAccountType().equals("SAVINGS"))
                .verifyComplete();

        verify(accountService).getAccountByNumber(accountNumber);
        verify(transactionRepository).createTransaction(any(TransactionCreateRequestDto.class));
        verify(accountService).updateAccountBalance(eq(accountNumber), any(BigDecimal.class));
    }

    @Test
    void createTransaction_WithInsufficientBalance_ShouldThrowBadRequestException() {
        // Given
        UUID customerId = UUID.randomUUID();
        String accountNumber = "ACC123456";

        TransactionCreateRequestDto request = TransactionCreateRequestDto.builder()
                .customerId(customerId)
                .sourceAccount(accountNumber)
                .amount(new BigDecimal("-600.00")) // Monto negativo para débito
                .build();

        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber(accountNumber)
                .initialBalance(new BigDecimal("500.00"))
                .customerId(customerId)
                .build();

        when(accountService.getAccountByNumber(accountNumber))
                .thenReturn(Mono.just(accountResponse));

        // When & Then
        StepVerifier.create(transactionService.createTransaction(request))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void createTransaction_WithWebClientBadRequest_ShouldThrowBadRequestException() {
        // Given
        UUID customerId = UUID.randomUUID();
        String accountNumber = "ACC123456";

        TransactionCreateRequestDto request = TransactionCreateRequestDto.builder()
                .customerId(customerId)
                .sourceAccount(accountNumber)
                .amount(new BigDecimal("100.00"))
                .build();

        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber(accountNumber)
                .initialBalance(new BigDecimal("500.00"))
                .customerId(customerId)
                .build();

        when(accountService.getAccountByNumber(accountNumber))
                .thenReturn(Mono.just(accountResponse));
        when(transactionRepository.createTransaction(any(TransactionCreateRequestDto.class)))
                .thenReturn(Mono.error(WebClientResponseException.create(400, "Bad Request", null, null, null)));

        // When & Then
        StepVerifier.create(transactionService.createTransaction(request))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void getTransactionById_WithValidId_ShouldReturnTransactionResponseDto() {
        // Given
        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String accountNumber = "ACC123456";

        TransactionResponseDto transactionResponse = TransactionResponseDto.builder()
                .transactionId(transactionId)
                .customerId(customerId)
                .sourceAccount(accountNumber)
                .amount(new BigDecimal("100.00"))
                .build();

        CustomerResponseDto customerResponse = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountNumber(accountNumber)
                .accountType("SAVINGS")
                .build();

        when(transactionRepository.getTransactionById(transactionId))
                .thenReturn(Mono.just(transactionResponse));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.just(customerResponse));
        when(accountRepository.getAccountByNumber(accountNumber))
                .thenReturn(Mono.just(accountResponse));

        // When & Then
        StepVerifier.create(transactionService.getTransactionById(transactionId))
                .expectNextMatches(response ->
                    response.getTransactionId().equals(transactionId) &&
                    response.getCustomerName().equals("John Doe") &&
                    response.getAccountType().equals("SAVINGS"))
                .verifyComplete();

        verify(transactionRepository).getTransactionById(transactionId);
    }

    @Test
    void getTransactionById_WithNotFoundError_ShouldThrowResourceNotFoundException() {
        // Given
        UUID transactionId = UUID.randomUUID();

        when(transactionRepository.getTransactionById(transactionId))
                .thenReturn(Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));

        // When & Then
        StepVerifier.create(transactionService.getTransactionById(transactionId))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void updateTransaction_WithValidData_ShouldReturnUpdatedTransaction() {
        // Given
        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String accountNumber = "ACC123456";

        TransactionUpdateRequestDto request = TransactionUpdateRequestDto.builder()
                .customerId(customerId)
                .sourceAccount(accountNumber)
                .amount(new BigDecimal("150.00"))
                .build();

        TransactionResponseDto updatedTransaction = TransactionResponseDto.builder()
                .transactionId(transactionId)
                .customerId(customerId)
                .sourceAccount(accountNumber)
                .amount(new BigDecimal("150.00"))
                .build();

        CustomerResponseDto customerResponse = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountNumber(accountNumber)
                .accountType("SAVINGS")
                .build();

        when(transactionRepository.updateTransaction(eq(transactionId), any(TransactionUpdateRequestDto.class)))
                .thenReturn(Mono.just(updatedTransaction));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.just(customerResponse));
        when(accountRepository.getAccountByNumber(accountNumber))
                .thenReturn(Mono.just(accountResponse));

        // When & Then
        StepVerifier.create(transactionService.updateTransaction(transactionId, request))
                .expectNextMatches(response ->
                    response.getAmount().equals(new BigDecimal("150.00")) &&
                    response.getTransactionId().equals(transactionId))
                .verifyComplete();
    }

    @Test
    void deleteTransaction_WithValidId_ShouldCompleteSuccessfully() {
        // Given
        UUID transactionId = UUID.randomUUID();

        when(transactionRepository.deleteTransaction(transactionId))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(transactionService.deleteTransaction(transactionId))
                .verifyComplete();

        verify(transactionRepository).deleteTransaction(transactionId);
    }

    @Test
    void getTransactionsByCustomerIdAndAccountId_WithValidData_ShouldReturnTransactions() {
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        TransactionResponseDto transaction2 = TransactionResponseDto.builder()
                .transactionId(UUID.randomUUID())
                .customerId(customerId)
                .sourceAccount(accountNumber)
                .amount(new BigDecimal("50.00"))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CustomerResponseDto customerResponse = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountNumber(accountNumber)
                .accountType("SAVINGS")
                .build();

        when(transactionRepository.getTransactionsByCustomerIdAndAccountId(
                customerId, accountNumber, startDate, endDate))
                .thenReturn(Flux.just(transaction1, transaction2));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.just(customerResponse));
        when(accountRepository.getAccountByNumber(accountNumber))
                .thenReturn(Mono.just(accountResponse));

        // When & Then
        StepVerifier.create(transactionService.getTransactionsByCustomerIdAndAccountId(
                customerId, accountNumber, startDate, endDate))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getAllActiveTransactions_ShouldReturnActiveTransactions() {
        // Given
        UUID customerId1 = UUID.randomUUID();
        UUID customerId2 = UUID.randomUUID();
        String accountNumber1 = "ACC123456";
        String accountNumber2 = "ACC789012";

        TransactionResponseDto transaction1 = TransactionResponseDto.builder()
                .transactionId(UUID.randomUUID())
                .customerId(customerId1)
                .sourceAccount(accountNumber1)
                .amount(new BigDecimal("100.00"))
                .build();

        TransactionResponseDto transaction2 = TransactionResponseDto.builder()
                .transactionId(UUID.randomUUID())
                .customerId(customerId2)
                .sourceAccount(accountNumber2)
                .amount(new BigDecimal("200.00"))
                .build();

        CustomerResponseDto customer1 = CustomerResponseDto.builder()
                .customerId(customerId1)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        CustomerResponseDto customer2 = CustomerResponseDto.builder()
                .customerId(customerId2)
                .party(PartyDto.builder().name("Jane Smith").build())
                .build();

        AccountResponseDto account1 = AccountResponseDto.builder()
                .accountNumber(accountNumber1)
                .accountType("SAVINGS")
                .build();

        AccountResponseDto account2 = AccountResponseDto.builder()
                .accountNumber(accountNumber2)
                .accountType("CHECKING")
                .build();

        when(transactionRepository.getAllActiveTransactions())
                .thenReturn(Flux.just(transaction1, transaction2));
        when(customerRepository.getCustomerById(customerId1))
                .thenReturn(Mono.just(customer1));
        when(customerRepository.getCustomerById(customerId2))
                .thenReturn(Mono.just(customer2));
        when(accountRepository.getAccountByNumber(accountNumber1))
                .thenReturn(Mono.just(account1));
        when(accountRepository.getAccountByNumber(accountNumber2))
                .thenReturn(Mono.just(account2));

        // When & Then
        StepVerifier.create(transactionService.getAllActiveTransactions())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void mapAdditionalData_WithCustomerNotFound_ShouldReturnUnknownCustomer() {
        // Given
        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        String accountNumber = "ACC123456";

        TransactionResponseDto transactionResponse = TransactionResponseDto.builder()
                .transactionId(transactionId)
                .customerId(customerId)
                .sourceAccount(accountNumber)
                .amount(new BigDecimal("100.00"))
                .build();

        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountNumber(accountNumber)
                .accountType("SAVINGS")
                .build();

        when(transactionRepository.getTransactionById(transactionId))
                .thenReturn(Mono.just(transactionResponse));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.error(new RuntimeException("Customer not found")));
        when(accountRepository.getAccountByNumber(accountNumber))
                .thenReturn(Mono.just(accountResponse));

        // When & Then
        StepVerifier.create(transactionService.getTransactionById(transactionId))
                .expectNextMatches(response ->
                    response.getCustomerName().equals("Unknown Customer") &&
                    response.getAccountType().equals("SAVINGS"))
                .verifyComplete();
    }
}
