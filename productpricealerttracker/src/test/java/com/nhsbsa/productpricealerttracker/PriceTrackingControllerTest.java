package com.nhsbsa.productpricealerttracker;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;
import com.nhsbsa.productpricealerttracker.enums.Frequency;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingRequest;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingResponse;
import com.nhsbsa.productpricealerttracker.notificationservice.EmailNotificationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestEntityManager
@ActiveProfiles("test")
@CrossOrigin(origins = "http://localhost:8080")
@TestPropertySource(locations = { "classpath:application-test.properties", "classpath:product-prices-test.json" })
class PriceTrackingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailNotificationService emailNotificationService;

    @Test
    void trackPrice_shouldReturn400_whenInValidRequest() throws Exception {
        // Given: Prepare mock data
        PriceTrackingRequest request = new PriceTrackingRequest();
        request.setProductUrl("https://example.com/product/123");
        request.setDesiredPrice(45.50);

        PriceTrackingResponse response = new PriceTrackingResponse();
        response.setMessage("Tracking initiated");

        // When & Then: Perform the request with Basic Authentication
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/pricetracker/createtrackrecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Basic " + encodeToBase64("user:Password@123"))) // Add Basic Auth header
                .andExpect(status().isBadRequest());
    }

    @Test
    void trackPrice_shouldReturn404_whenInValidproductURL() throws Exception {
        // Given: Prepare mock data
        PriceTrackingRequest request = new PriceTrackingRequest();
        request.setUserName("sindhu");
        request.setEmailId("abc@test.com");
        request.setCheckFrequency(Frequency.MORNING_ONLY);
        request.setProductUrl("https://example.com/product/123");
        request.setDesiredPrice(45.50);

        PriceTrackingResponse response = new PriceTrackingResponse();
        response.setMessage("Tracking initiated");

        // When & Then: Perform the request with Basic Authentication
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/pricetracker/createtrackrecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Basic " + encodeToBase64("user:Password@123"))) // Add Basic Auth header
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product url not listed in tracker. Please enter valid url"));

    }

    @Test
    void trackPrice_shouldReturn200_whenValidRequest() throws Exception {
        // Given: Prepare mock data
        PriceTrackingRequest request = new PriceTrackingRequest();
        request.setUserName("sindhu");
        request.setEmailId("abc@test.com");
        request.setCheckFrequency(Frequency.MORNING_ONLY);
        request.setProductUrl("https://example.com/product1");
        request.setDesiredPrice(45.50);

        PriceTrackingResponse response = new PriceTrackingResponse();
        response.setMessage("Tracking initiated");

        // When & Then: Perform the request with Basic Authentication
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/pricetracker/createtrackrecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Basic " + encodeToBase64("user:Password@123"))) // Add Basic Auth header
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(
                        "You've successfully registered for price tracking. An alert will be sent to your email when the price drops."));
    }

    @Test
    void trackPrice_shouldReturn500_whenErrorWhileSendingEmail() throws Exception {
        PriceTrackingRequest request = new PriceTrackingRequest();
        request.setUserName("sindhu");
        request.setEmailId("abc@test.com");
        request.setCheckFrequency(Frequency.MORNING_ONLY);
        request.setProductUrl("https://example.com/product4");
        request.setDesiredPrice(50.50);

        // PriceTrackingResponse response = new PriceTrackingResponse();
        // response.setMessage("Tracking initiated");
         doThrow(new RuntimeException("Failed to send email"))
        .when(emailNotificationService).sendNotification(any(UserPriceTracker.class), anyString());


        // When & Then: Perform the request with Basic Authentication
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/pricetracker/createtrackrecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Basic " + encodeToBase64("user:Password@123"))) // Add Basic Auth header
                .andExpect(status().isInternalServerError());
    }

    @Test
    void trackPrice_shouldReturn200_whenDesiredPriceMatch() throws Exception {
        // Given: Prepare mock data
        PriceTrackingRequest request = new PriceTrackingRequest();
        request.setUserName("sindhu");
        request.setEmailId("abc@test.com");
        request.setCheckFrequency(Frequency.MORNING_ONLY);
        request.setProductUrl("https://example.com/product2");
        request.setDesiredPrice(100.50);

        PriceTrackingResponse response = new PriceTrackingResponse();
        response.setMessage("Tracking initiated");

        // When & Then: Perform the request with Basic Authentication
        mockMvc.perform(MockMvcRequestBuilders.post("/v1/pricetracker/createtrackrecord")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .header("Authorization", "Basic " + encodeToBase64("user:Password@123"))) // Add Basic Auth header
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message")
                        .value("Price drop detected — we've sent an alert to your registered email"));
    }

    private String encodeToBase64(String credentials) {
        return new String(Base64.getEncoder().encode(credentials.getBytes()));
    }

}
