package com.banking.cdeh_msa_bp_banking_application.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import java.util.concurrent.TimeUnit;

@Configuration
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class WebClientConfiguration {
    final ApplicationProperties properties;
    @Bean
    public WebClient webClient() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, properties.getConnectTimeout())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(
                            new ReadTimeoutHandler(properties.getReadTimeout(), TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(
                            new WriteTimeoutHandler(properties.getReadTimeout(), TimeUnit.MILLISECONDS));
                });
        return WebClient.builder()
                .filter(ExchangeFilterFunction.ofResponseProcessor(this::logHttpErrors))
                .codecs(configure -> configure.defaultCodecs().maxInMemorySize(properties.getMaxInMemorySize()))
                .clientConnector(new ReactorClientHttpConnector(httpClient)).build();
    }
    private Mono<ClientResponse> logHttpErrors(ClientResponse clientResponse) {
        if(clientResponse.statusCode().isError()){
            return clientResponse.bodyToMono(String.class)
                    .map(body -> {
                        log.error(String.format("Status: %s, body: %s", clientResponse.statusCode().toString(), body));
                        return clientResponse.mutate().body(body).build();
                    });
        }
        return Mono.just(clientResponse);
    }
}

