package com.banking.cdeh_msa_bp_banking_application.controller;

import com.banking.cdeh_msa_bp_banking_application.service.CustomerService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.PartyDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebFluxTest(controllers = CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CustomerService customerService;

    @Test
    void createCustomer_WithValidData_ShouldReturnCreatedCustomer() {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerRequestDto request = CustomerRequestDto.builder()
                .party(PartyDto.builder()
                        .name("John Doe")
                        .identification("12345678")
                        .build())
                .customer(CustomerDto.builder()
                        .password("password123")
                        .build())
                .build();

        CustomerResponseDto expectedResponse = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(request.getParty())
                .password(request.getCustomer().getPassword())
                .build();

        when(customerService.createCustomer(any(CustomerRequestDto.class)))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.post()
                .uri("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerResponseDto.class)
                .value(response -> {
                    assert response.getCustomerId().equals(customerId);
                    assert response.getParty().getName().equals("John Doe");
                });

        verify(customerService).createCustomer(any(CustomerRequestDto.class));
    }

    @Test
    void getCustomerById_WithValidId_ShouldReturnCustomer() {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerResponseDto expectedResponse = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(PartyDto.builder()
                        .name("John Doe")
                        .identification("12345678")
                        .build())
                .build();

        when(customerService.getCustomerById(customerId))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.get()
                .uri("/api/clientes/{customerId}", customerId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseDto.class)
                .value(response -> {
                    assert response.getCustomerId().equals(customerId);
                    assert response.getParty().getName().equals("John Doe");
                });

        verify(customerService).getCustomerById(customerId);
    }

    @Test
    void getAllCustomers_ShouldReturnListOfCustomers() {
        // Given
        CustomerResponseDto customer1 = CustomerResponseDto.builder()
                .customerId(UUID.randomUUID())
                .party(PartyDto.builder().name("John Doe").build())
                .build();

        CustomerResponseDto customer2 = CustomerResponseDto.builder()
                .customerId(UUID.randomUUID())
                .party(PartyDto.builder().name("Jane Smith").build())
                .build();

        when(customerService.getAllCustomers())
                .thenReturn(Flux.just(customer1, customer2));

        // When & Then
        webTestClient.get()
                .uri("/api/clientes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerResponseDto.class)
                .hasSize(2);

        verify(customerService).getAllCustomers();
    }

    @Test
    void updateCustomer_WithValidData_ShouldReturnUpdatedCustomer() {
        // Given
        UUID customerId = UUID.randomUUID();
        CustomerRequestDto request = CustomerRequestDto.builder()
                .party(PartyDto.builder()
                        .name("John Updated")
                        .identification("12345678")
                        .build())
                .customer(CustomerDto.builder()
                        .password("newpassword")
                        .build())
                .build();

        CustomerResponseDto expectedResponse = CustomerResponseDto.builder()
                .customerId(customerId)
                .party(request.getParty())
                .password(request.getCustomer().getPassword())
                .build();

        when(customerService.updateCustomer(eq(customerId), any(CustomerRequestDto.class)))
                .thenReturn(Mono.just(expectedResponse));

        // When & Then
        webTestClient.put()
                .uri("/api/clientes/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerResponseDto.class)
                .value(response -> {
                    assert response.getCustomerId().equals(customerId);
                    assert response.getParty().getName().equals("John Updated");
                });

        verify(customerService).updateCustomer(eq(customerId), any(CustomerRequestDto.class));
    }

    @Test
    void deleteCustomer_WithValidId_ShouldReturnNoContent() {
        // Given
        UUID customerId = UUID.randomUUID();

        when(customerService.deleteCustomer(customerId))
                .thenReturn(Mono.empty());

        // When & Then
        webTestClient.delete()
                .uri("/api/clientes/{customerId}", customerId)
                .exchange()
                .expectStatus().isNoContent();

        verify(customerService).deleteCustomer(customerId);
    }
}
