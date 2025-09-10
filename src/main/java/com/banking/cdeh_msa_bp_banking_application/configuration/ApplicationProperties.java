package com.banking.cdeh_msa_bp_banking_application.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static lombok.AccessLevel.PRIVATE;

@Data
@ConfigurationProperties(prefix = "application")
@FieldDefaults(level = PRIVATE)
public class ApplicationProperties {
    Integer connectTimeout;
    Integer readTimeout;
    Integer maxInMemorySize;
    Url url;

    @Getter
    @Setter
    @FieldDefaults(level = PRIVATE)
    public static class Url {
        ServiceConfig partyService;
        ServiceConfig accountService;
        ServiceConfig transactionService;
    }

    @Getter
    @Setter
    @FieldDefaults(level = PRIVATE)
    public static class ServiceConfig {
        String baseUrl;
        String path;

        public String getFullUrl() {
            return baseUrl + path;
        }
    }
}
