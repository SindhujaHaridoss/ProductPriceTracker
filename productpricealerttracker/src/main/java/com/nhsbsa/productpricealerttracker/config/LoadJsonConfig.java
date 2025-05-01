package com.nhsbsa.productpricealerttracker.config;

import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhsbsa.productpricealerttracker.model.ProductPrice;

import jakarta.annotation.PostConstruct;

@Component
public class LoadJsonConfig {
    private static final Logger log = LoggerFactory.getLogger(LoadJsonConfig.class);

    private List<ProductPrice> productPrices;

    private static final String JSON_FILE_PATH = "product-prices.json"; // Path to the JSON file

    @PostConstruct
    public void loadJsonOnce() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(JSON_FILE_PATH)) {
            if (is == null) {
                log.error("JSON file {} not found in resources.", JSON_FILE_PATH);
                return;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            productPrices = objectMapper.readValue(is, new TypeReference<List<ProductPrice>>() {
            });
            log.info("Product prices loaded successfully.");
        } catch (Exception e) {
            log.error("Failed to read product prices JSON", e);
        }
    }

    @Bean
    public List<ProductPrice> productPrices() {
        return productPrices;
    }
}
