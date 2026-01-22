package com.larissa.contracts;

import com.larissa.contracts.infrastructure.config.AppKafkaProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppKafkaProperties.class)
public class ContractValidationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContractValidationApplication.class, args);
	}
}
