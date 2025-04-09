package com.CampusCart.Campus_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.CampusCart.Campus_backend.model")
@EnableJpaRepositories("com.CampusCart.Campus_backend.repository") 
public class CampusBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusBackendApplication.class, args);
    }
}
