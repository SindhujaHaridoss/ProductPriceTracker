package com.nhsbsa.productpricealerttracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJpaRepositories({ "com.nhsbsa.productpricealerttracker.repository" })
@EntityScan({ "com.nhsbsa.productpricealerttracker.entity" })
public class ProductPriceAlertTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductPriceAlertTrackerApplication.class, args);
	}
}
