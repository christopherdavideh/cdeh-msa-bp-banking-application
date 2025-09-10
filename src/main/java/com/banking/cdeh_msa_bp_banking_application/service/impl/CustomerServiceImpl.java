package com.banking.cdeh_msa_bp_banking_application.service.impl;

import com.banking.cdeh_msa_bp_banking_application.exception.BadRequestException;
import com.banking.cdeh_msa_bp_banking_application.exception.ResourceNotFoundException;
import com.banking.cdeh_msa_bp_banking_application.helper.ValidationHelper;
import com.banking.cdeh_msa_bp_banking_application.repository.CustomerRepository;
import com.banking.cdeh_msa_bp_banking_application.service.CustomerService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerResponseDto;
import com.banking.cdeh_msa_bp_banking_application.util.LogMessages;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomerServiceImpl implements CustomerService {

    CustomerRepository customerRepository;

    @Override
    public Mono<CustomerResponseDto> createCustomer(CustomerRequestDto customerRequestDto) {
        return validateCustomerRequest(customerRequestDto)
                .then(customerRepository.createCustomer(customerRequestDto))
                .doFirst(() -> log.info(LogMessages.CUSTOMER_CREATE_START,
                        customerRequestDto.getParty().getIdentification()))
                .doOnSuccess(response -> log.info(LogMessages.CUSTOMER_CREATE_SUCCESS,
                        response.getCustomerId()))
                .doOnError(error -> log.error(LogMessages.CUSTOMER_CREATE_ERROR, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.BadRequest.class,
                        ex -> Mono.error(new BadRequestException("Invalid customer data")))
                .onErrorResume(WebClientResponseException.Conflict.class,
                        ex -> Mono.error(new BadRequestException("Customer already exists")));
    }

    @Override
    public Mono<CustomerResponseDto> getCustomerById(UUID customerId) {
        return ValidationHelper.validateCustomerId(customerId)
                .then(customerRepository.getCustomerById(customerId))
                .doFirst(() -> log.info(LogMessages.CUSTOMER_GET_BY_ID_START, customerId))
                .doOnSuccess(response -> log.info(LogMessages.CUSTOMER_GET_BY_ID_SUCCESS,
                        response.getParty().getName()))
                .doOnError(error -> log.error(LogMessages.CUSTOMER_GET_BY_ID_ERROR, customerId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Customer not found with ID: " + customerId)));
    }

    @Override
    public Flux<CustomerResponseDto> getAllCustomers() {
        return customerRepository.getAllCustomers()
                .doFirst(() -> log.info(LogMessages.CUSTOMER_GET_ALL_START))
                .doOnComplete(() -> log.info(LogMessages.CUSTOMER_GET_ALL_SUCCESS))
                .doOnError(error -> log.error(LogMessages.CUSTOMER_GET_ALL_ERROR, error.getMessage()));
    }

    @Override
    public Mono<CustomerResponseDto> updateCustomer(UUID customerId, CustomerRequestDto customerRequestDto) {
        return ValidationHelper.validateCustomerId(customerId)
                .then(validateCustomerRequest(customerRequestDto))
                .then(customerRepository.updateCustomer(customerId, customerRequestDto))
                .doFirst(() -> log.info(LogMessages.CUSTOMER_UPDATE_START, customerId))
                .doOnSuccess(response -> log.info(LogMessages.CUSTOMER_UPDATE_SUCCESS,
                        response.getCustomerId()))
                .doOnError(error -> log.error(LogMessages.CUSTOMER_UPDATE_ERROR, customerId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Customer not found with ID: " + customerId)))
                .onErrorResume(WebClientResponseException.BadRequest.class,
                        ex -> Mono.error(new BadRequestException("Invalid customer data")));
    }

    @Override
    public Mono<Void> deleteCustomer(UUID customerId) {
        return ValidationHelper.validateCustomerId(customerId)
                .then(customerRepository.deleteCustomer(customerId))
                .doFirst(() -> log.info(LogMessages.CUSTOMER_DELETE_START, customerId))
                .doOnSuccess(response -> log.info(LogMessages.CUSTOMER_DELETE_SUCCESS, customerId))
                .doOnError(error -> log.error(LogMessages.CUSTOMER_DELETE_ERROR, customerId, error.getMessage()))
                .onErrorResume(IllegalArgumentException.class,
                        ex -> Mono.error(new BadRequestException(ex.getMessage())))
                .onErrorResume(WebClientResponseException.NotFound.class,
                        ex -> Mono.error(new ResourceNotFoundException("Customer not found with ID: " + customerId)));
    }

    private Mono<Void> validateCustomerRequest(CustomerRequestDto customerRequestDto) {
        return Mono.fromRunnable(() -> {
            if (customerRequestDto == null) {
                throw new IllegalArgumentException("Customer request cannot be null");
            }
            if (customerRequestDto.getParty() == null) {
                throw new IllegalArgumentException("Party information is required");
            }
            if (customerRequestDto.getCustomer() == null) {
                throw new IllegalArgumentException("Customer information is required");
            }
            if (customerRequestDto.getParty().getName() == null || customerRequestDto.getParty().getName().trim().isEmpty()) {
                throw new IllegalArgumentException("Customer name is required");
            }
            if (customerRequestDto.getParty().getIdentification() == null || customerRequestDto.getParty().getIdentification().trim().isEmpty()) {
                throw new IllegalArgumentException("Customer identification is required");
            }
            if (customerRequestDto.getCustomer().getPassword() == null || customerRequestDto.getCustomer().getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Customer password is required");
            }
        });
    }
}
