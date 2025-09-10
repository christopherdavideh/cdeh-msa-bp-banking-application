package com.banking.cdeh_msa_bp_banking_application.controller;

import com.banking.cdeh_msa_bp_banking_application.service.CustomerService;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerRequestDto;
import com.banking.cdeh_msa_bp_banking_application.service.dto.CustomerResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public Mono<ResponseEntity<CustomerResponseDto>> createCustomer(
            @Valid @RequestBody CustomerRequestDto customerRequestDto) {

        return customerService.createCustomer(customerRequestDto)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/{customerId}")
    public Mono<ResponseEntity<CustomerResponseDto>> getCustomerById(@PathVariable UUID customerId) {

        return customerService.getCustomerById(customerId)
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<CustomerResponseDto>>> getAllCustomers() {

        return Mono.just(ResponseEntity.ok(customerService.getAllCustomers()));
    }

    @PutMapping("/{customerId}")
    public Mono<ResponseEntity<CustomerResponseDto>> updateCustomer(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerRequestDto customerRequestDto) {

        return customerService.updateCustomer(customerId, customerRequestDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/{customerId}")
    public Mono<ResponseEntity<Void>> deleteCustomer(@PathVariable UUID customerId) {

        return customerService.deleteCustomer(customerId)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }
}
