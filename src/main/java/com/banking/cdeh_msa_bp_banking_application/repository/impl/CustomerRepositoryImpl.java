package com.banking.cdeh_msa_bp_banking_application.repository.impl;

import com.banking.cdeh_msa_bp_banking_application.configuration.ApplicationProperties;
import com.banking.cdeh_msa_bp_banking_application.repository.CustomerRepository;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerResponseDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomerRepositoryImpl implements CustomerRepository {

    WebClient webClient;
    ApplicationProperties applicationProperties;

    @Override
    public Mono<CustomerResponseDto> createCustomer(CustomerRequestDto customerRequestDto) {

        return webClient.post()
                .uri(applicationProperties.getUrl().getPartyService().getFullUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerRequestDto)
                .retrieve()
                .bodyToMono(CustomerResponseDto.class);
    }

    @Override
    public Mono<CustomerResponseDto> getCustomerById(UUID customerId) {

        return webClient.get()
                .uri(applicationProperties.getUrl().getPartyService().getFullUrl() + "/{customerId}", customerId)
                .retrieve()
                .bodyToMono(CustomerResponseDto.class);
    }

    @Override
    public Flux<CustomerResponseDto> getAllCustomers() {

        return webClient.get()
                .uri(applicationProperties.getUrl().getPartyService().getFullUrl())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CustomerResponseDto>>() {})
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<CustomerResponseDto> updateCustomer(UUID customerId, CustomerRequestDto customerRequestDto) {

        return webClient.put()
                .uri(applicationProperties.getUrl().getPartyService().getFullUrl() + "/{customerId}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerRequestDto)
                .retrieve()
                .bodyToMono(CustomerResponseDto.class);
    }

    @Override
    public Mono<Void> deleteCustomer(UUID customerId) {

        return webClient.delete()
                .uri(applicationProperties.getUrl().getPartyService().getFullUrl() + "/{customerId}", customerId)
                .retrieve()
                .bodyToMono(Void.class);
    }
}
