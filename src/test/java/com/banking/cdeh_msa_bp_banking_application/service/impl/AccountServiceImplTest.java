package com.banking.cdeh_msa_bp_banking_application.service.impl;

import com.banking.cdeh_msa_bp_banking_application.exception.BadRequestException;
import com.banking.cdeh_msa_bp_banking_application.exception.ResourceNotFoundException;
import com.banking.cdeh_msa_bp_banking_application.repository.AccountRepository;
import com.banking.cdeh_msa_bp_banking_application.repository.CustomerRepository;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.AccountResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.PartyDto;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void createAccount_WithValidData_ShouldReturnAccountResponseDto() {
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

        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountId(accountId)
                .accountNumber("ACC123456")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("1000.00"))
                .accountStatus(true)
                .customerId(customerId)
                .build();

        CustomerResponseDto customerResponse = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        when(accountRepository.createAccount(any(AccountRequestDto.class)))
                .thenReturn(Mono.just(accountResponse));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.just(customerResponse));

        // When & Then
        StepVerifier.create(accountService.createAccount(request))
                .expectNextMatches(response ->
                    response.getAccountId().equals(accountId) &&
                    response.getCustomerName().equals("John Doe"))
                .verifyComplete();

        verify(accountRepository).createAccount(request);
        verify(customerRepository).getCustomerById(customerId);
    }

    @Test
    void createAccount_WithWebClientBadRequest_ShouldThrowBadRequestException() {
        // Given
        AccountRequestDto request = AccountRequestDto.builder()
                .accountNumber("ACC123456")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("1000.00"))
                .accountStatus(true)
                .customerId(UUID.randomUUID())
                .build();

        when(accountRepository.createAccount(any(AccountRequestDto.class)))
                .thenReturn(Mono.error(WebClientResponseException.create(400, "Bad Request", null, null, null)));

        // When & Then
        StepVerifier.create(accountService.createAccount(request))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void createAccount_WithWebClientConflict_ShouldThrowBadRequestException() {
        // Given
        AccountRequestDto request = AccountRequestDto.builder()
                .accountNumber("ACC123456")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("1000.00"))
                .accountStatus(true)
                .customerId(UUID.randomUUID())
                .build();

        when(accountRepository.createAccount(any(AccountRequestDto.class)))
                .thenReturn(Mono.error(WebClientResponseException.create(409, "Conflict", null, null, null)));

        // When & Then
        StepVerifier.create(accountService.createAccount(request))
                .expectError(BadRequestException.class)
                .verify();
    }

    @Test
    void getAccountById_WithValidId_ShouldReturnAccountResponseDto() {
        // Given
        UUID accountId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountId(accountId)
                .accountNumber("ACC123456")
                .customerId(customerId)
                .build();

        CustomerResponseDto customerResponse = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        when(accountRepository.getAccountById(accountId))
                .thenReturn(Mono.just(accountResponse));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.just(customerResponse));

        // When & Then
        StepVerifier.create(accountService.getAccountById(accountId))
                .expectNextMatches(response ->
                    response.getAccountId().equals(accountId) &&
                    response.getCustomerName().equals("John Doe"))
                .verifyComplete();

        verify(accountRepository).getAccountById(accountId);
        verify(customerRepository).getCustomerById(customerId);
    }

    @Test
    void getAccountById_WithNotFoundError_ShouldThrowResourceNotFoundException() {
        // Given
        UUID accountId = UUID.randomUUID();

        when(accountRepository.getAccountById(accountId))
                .thenReturn(Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));

        // When & Then
        StepVerifier.create(accountService.getAccountById(accountId))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void getAccountByNumber_WithValidNumber_ShouldReturnAccountResponseDto() {
        // Given
        String accountNumber = "ACC123456";
        UUID customerId = UUID.randomUUID();
        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber(accountNumber)
                .customerId(customerId)
                .build();

        CustomerResponseDto customerResponse = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        when(accountRepository.getAccountByNumber(accountNumber))
                .thenReturn(Mono.just(accountResponse));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.just(customerResponse));

        // When & Then
        StepVerifier.create(accountService.getAccountByNumber(accountNumber))
                .expectNextMatches(response ->
                    response.getAccountNumber().equals(accountNumber) &&
                    response.getCustomerName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    void getAllAccounts_ShouldReturnFluxOfAccounts() {
        // Given
        UUID customerId1 = UUID.randomUUID();
        UUID customerId2 = UUID.randomUUID();

        AccountResponseDto account1 = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("ACC123456")
                .customerId(customerId1)
                .build();

        AccountResponseDto account2 = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("ACC789012")
                .customerId(customerId2)
                .build();

        CustomerResponseDto customer1 = CustomerResponseDto.builder()
                .customerId(customerId1)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        CustomerResponseDto customer2 = CustomerResponseDto.builder()
                .customerId(customerId2)
                .party(PartyDto.builder().name("Jane Smith").build())
                .build();

        when(accountRepository.getAllAccounts())
                .thenReturn(Flux.just(account1, account2));
        when(customerRepository.getCustomerById(customerId1))
                .thenReturn(Mono.just(customer1));
        when(customerRepository.getCustomerById(customerId2))
                .thenReturn(Mono.just(customer2));

        // When & Then
        StepVerifier.create(accountService.getAllAccounts())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void getAccountsByCustomerId_WithValidCustomerId_ShouldReturnFluxOfAccounts() {
        // Given
        UUID customerId = UUID.randomUUID();
        AccountResponseDto account = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("ACC123456")
                .customerId(customerId)
                .build();

        CustomerResponseDto customer = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        when(accountRepository.getAccountsByCustomerId(customerId))
                .thenReturn(Flux.just(account));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.just(customer));

        // When & Then
        StepVerifier.create(accountService.getAccountsByCustomerId(customerId))
                .expectNextMatches(response ->
                    response.getCustomerId().equals(customerId) &&
                    response.getCustomerName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    void updateAccount_WithValidData_ShouldReturnUpdatedAccount() {
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

        AccountResponseDto updatedAccount = AccountResponseDto.builder()
                .accountId(accountId)
                .accountNumber("ACC123456")
                .accountType("CHECKING")
                .initialBalance(new BigDecimal("2000.00"))
                .accountStatus(true)
                .customerId(customerId)
                .build();

        CustomerResponseDto customer = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        when(accountRepository.updateAccount(eq(accountId), any(AccountRequestDto.class)))
                .thenReturn(Mono.just(updatedAccount));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.just(customer));

        // When & Then
        StepVerifier.create(accountService.updateAccount(accountId, request))
                .expectNextMatches(response ->
                    response.getAccountType().equals("CHECKING") &&
                    response.getCustomerName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    void updateAccountBalance_WithValidData_ShouldReturnUpdatedAccount() {
        // Given
        String accountNumber = "ACC123456";
        BigDecimal newBalance = new BigDecimal("1500.00");
        UUID customerId = UUID.randomUUID();

        AccountResponseDto updatedAccount = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber(accountNumber)
                .initialBalance(newBalance)
                .customerId(customerId)
                .build();

        CustomerResponseDto customer = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        when(accountRepository.updateAccountBalance(accountNumber, newBalance))
                .thenReturn(Mono.just(updatedAccount));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.just(customer));

        // When & Then
        StepVerifier.create(accountService.updateAccountBalance(accountNumber, newBalance))
                .expectNextMatches(response ->
                    response.getInitialBalance().equals(newBalance) &&
                    response.getCustomerName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    void deleteAccount_WithValidId_ShouldCompleteSuccessfully() {
        // Given
        UUID accountId = UUID.randomUUID();

        when(accountRepository.deleteAccount(accountId))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(accountService.deleteAccount(accountId))
                .verifyComplete();

        verify(accountRepository).deleteAccount(accountId);
    }

    @Test
    void deleteAccount_WithNotFoundError_ShouldThrowResourceNotFoundException() {
        // Given
        UUID accountId = UUID.randomUUID();

        when(accountRepository.deleteAccount(accountId))
                .thenReturn(Mono.error(WebClientResponseException.create(404, "Not Found", null, null, null)));

        // When & Then
        StepVerifier.create(accountService.deleteAccount(accountId))
                .expectError(ResourceNotFoundException.class)
                .verify();
    }

    @Test
    void mapCustomerName_WithCustomerNotFound_ShouldReturnOriginalAccount() {
        // Given
        UUID customerId = UUID.randomUUID();
        AccountResponseDto accountResponse = AccountResponseDto.builder()
                .accountId(UUID.randomUUID())
                .accountNumber("ACC123456")
                .customerId(customerId)
                .build();

        when(accountRepository.getAccountById(any(UUID.class)))
                .thenReturn(Mono.just(accountResponse));
        when(customerRepository.getCustomerById(customerId))
                .thenReturn(Mono.error(new RuntimeException("Customer not found")));

        // When & Then
        StepVerifier.create(accountService.getAccountById(UUID.randomUUID()))
                .expectNextMatches(response ->
                    response.getCustomerName() == null)
                .verifyComplete();
    }
}
