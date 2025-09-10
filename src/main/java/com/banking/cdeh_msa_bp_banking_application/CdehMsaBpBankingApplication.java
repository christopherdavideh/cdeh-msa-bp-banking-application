package com.banking.cdeh_msa_bp_banking_application;

import com.banking.cdeh_msa_bp_banking_application.configuration.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
public class CdehMsaBpBankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(CdehMsaBpBankingApplication.class, args);
	}

}
