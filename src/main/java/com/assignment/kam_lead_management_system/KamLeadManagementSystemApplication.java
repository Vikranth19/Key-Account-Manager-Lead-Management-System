package com.assignment.kam_lead_management_system;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
		info = @Info(
				description = "KAM Lead management system",
				version = "1.0.0",
				title = "Documentation for KAM lead management system"
		)
)
@SpringBootApplication
public class KamLeadManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(KamLeadManagementSystemApplication.class, args);
	}

}
