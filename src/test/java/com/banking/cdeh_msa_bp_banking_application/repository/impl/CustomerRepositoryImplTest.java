package com.banking.cdeh_msa_bp_banking_application.repository.impl;

import com.banking.cdeh_msa_bp_banking_application.configuration.ApplicationProperties;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerResponseDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.PartyDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerDto;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryImplTest {

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private ApplicationProperties.Url url;

    @Mock
    private ApplicationProperties.ServiceConfig partyService;

    @InjectMocks
    private CustomerRepositoryImpl customerRepository;

    private MockWebServer mockWebServer;
    private WebClient webClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        customerRepository = new CustomerRepositoryImpl(webClient, applicationProperties);

        when(applicationProperties.getUrl()).thenReturn(url);
        when(url.getPartyService()).thenReturn(partyService);
        when(partyService.getFullUrl()).thenReturn(mockWebServer.url("/parties").toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void createCustomer_ShouldReturnCustomerResponseDto() {
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
                .status(request.getCustomer().getStatus())
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.CREATED.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "customerId": "%s",
                        "party": {
                            "name": "John Doe",
                            "identification": "12345678"
                        },
                        "customer": {
                            "password": "password123"
                        }
                    }
                    """.formatted(customerId)));

        // When & Then
        StepVerifier.create(customerRepository.createCustomer(request))
                .expectNextMatches(response ->
                    response.getCustomerId().equals(customerId) &&
                    response.getParty().getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    void getCustomerById_ShouldReturnCustomerResponseDto() {
        // Given
        UUID customerId = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "customerId": "%s",
                        "party": {
                            "name": "John Doe",
                            "identification": "12345678"
                        },
                        "customer": {
                            "password": "password123"
                        }
                    }
                    """.formatted(customerId)));

        // When & Then
        StepVerifier.create(customerRepository.getCustomerById(customerId))
                .expectNextMatches(response ->
                    response.getCustomerId().equals(customerId) &&
                    response.getParty().getName().equals("John Doe"))
                .verifyComplete();
    }

    @Test
    void getAllCustomers_ShouldReturnFluxOfCustomers() {
        // Given
        UUID customerId1 = UUID.randomUUID();
        UUID customerId2 = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    [
                        {
                            "customerId": "%s",
                            "party": {
                                "name": "John Doe",
                                "identification": "12345678"
                            },
                            "customer": {
                                "password": "password123"
                            }
                        },
                        {
                            "customerId": "%s",
                            "party": {
                                "name": "Jane Smith",
                                "identification": "87654321"
                            },
                            "customer": {
                                "password": "password456"
                            }
                        }
                    ]
                    """.formatted(customerId1, customerId2)));

        // When & Then
        StepVerifier.create(customerRepository.getAllCustomers())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer() {
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

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .setBody("""
                    {
                        "customerId": "%s",
                        "party": {
                            "name": "John Updated",
                            "identification": "12345678"
                        },
                        "customer": {
                            "password": "newpassword"
                        }
                    }
                    """.formatted(customerId)));

        // When & Then
        StepVerifier.create(customerRepository.updateCustomer(customerId, request))
                .expectNextMatches(response ->
                    response.getParty().getName().equals("John Updated"))
                .verifyComplete();
    }

    @Test
    void deleteCustomer_ShouldReturnVoid() {
        // Given
        UUID customerId = UUID.randomUUID();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NO_CONTENT.value()));

        // When & Then
        StepVerifier.create(customerRepository.deleteCustomer(customerId))
                .verifyComplete();
    }
}
