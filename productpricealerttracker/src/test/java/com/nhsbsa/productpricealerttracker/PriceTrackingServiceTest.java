package com.nhsbsa.productpricealerttracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nhsbsa.productpricealerttracker.controller.PriceTrackingController;
import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;
import com.nhsbsa.productpricealerttracker.enums.Frequency;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingRequest;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingResponse;
import com.nhsbsa.productpricealerttracker.model.ProductPrice;
import com.nhsbsa.productpricealerttracker.notificationservice.EmailNotificationService;
import com.nhsbsa.productpricealerttracker.repository.PriceTrackingRepository;
import com.nhsbsa.productpricealerttracker.service.PriceTrackingService;
import com.nhsbsa.productpricealerttracker.util.PriceAlertUtil;

class PriceTrackingServiceTest {

    @InjectMocks
    private PriceTrackingService priceTrackingService;

    @Mock
    private PriceTrackingRepository priceTrackingRepository;

    @Mock
    private PriceAlertUtil priceAlertUtil;

    @Mock
    private EmailNotificationService emailNotificationService;

    @InjectMocks
    private PriceTrackingController priceTrackingController;

    private PriceTrackingRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize mock request
        request = new PriceTrackingRequest();
        request.setUserName("testUser");
        request.setProductUrl("https://example.com/product1");
        request.setDesiredPrice(100.00);
        request.setEmailId("abc@gmail.com");
        request.setCheckFrequency(Frequency.MORNING_ONLY);
    }


@Test
    void testTrackUserProductPrice_NewRecord() {
        // Arrange
        PriceTrackingRequest request = new PriceTrackingRequest("testuser", "https://example.com/product1", 100.00, Frequency.MORNING_ONLY, "abc@sdf.com");
        ProductPrice productPrice = new ProductPrice("https://example.com/product1", 90.00);
        UserPriceTracker newUserPriceTracker = new UserPriceTracker(null, "testuser", "https://example.com/product1", 100.00, Frequency.MORNING_ONLY, false, "testuser@example.com", 0.00);
        
        when(priceAlertUtil.getProductPrices()).thenReturn(List.of(productPrice));
        when(priceTrackingRepository.findByUserNameAndProductUrl("testuser", "https://example.com/product1")).thenReturn(Collections.emptyList());
        when(priceAlertUtil.createProductPriceAndSendAlert(request)).thenReturn(new PriceTrackingResponse(
                "Price tracker registered successfully", false, "testuser", "https://example.com/product1", 0, 100.00));

        // Act
        PriceTrackingResponse response = priceTrackingService.trackUserProductPrice(request);

        // Assert
        assertNotNull(response);
        assertEquals("Price tracker registered successfully", response.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        PriceTrackingRequest productNotFoundrequest = new PriceTrackingRequest();
        productNotFoundrequest.setProductUrl("https://nonexistent.com/product");
        productNotFoundrequest.setUserName("john");

        // Mock a list that does NOT contain the product
        when(priceAlertUtil.getProductPrices()).thenReturn(List.of(
                new ProductPrice("https://example.com/product1", 100.0)));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            priceTrackingService.trackUserProductPrice(productNotFoundrequest);
        });

        assertEquals("Product url not listed in tracker. Please enter valid url", exception.getMessage());
    }

    @Test
    void testTrackUserProductPrice_EmptyUserPriceRecords() {
        // Mock the product prices list
        List<ProductPrice> mockProductPrices = Arrays.asList(
                new ProductPrice("https://example.com/product1", 50.00));
        when(priceAlertUtil.getProductPrices()).thenReturn(mockProductPrices);

        // Mock the user price records
        when(priceTrackingRepository.findByUserNameAndProductUrl(request.getUserName(), request.getProductUrl()))
                .thenReturn(Collections.emptyList());

        // Mock the create product price and send alert
        when(priceAlertUtil.createProductPriceAndSendAlert(any(PriceTrackingRequest.class)))
                .thenReturn(new PriceTrackingResponse(
                        "Mock response",
                        true,
                        "testUser",
                        "https://example.com/product1",
                        99.99,
                        120.00));

        // Call the service method
        priceTrackingService.trackUserProductPrice(request);

        // Verify that createProductPriceAndSendAlert was called
        verify(priceAlertUtil, times(1)).createProductPriceAndSendAlert(request);
    }

    @Test
    void testTrackUserProductPrice_NewUser_ShouldCallCreateAndSendAlert() {
        // Arrange
        PriceTrackingRequest createAndSendAlertrequest = new PriceTrackingRequest();
        createAndSendAlertrequest.setUserName("testuser");
        createAndSendAlertrequest.setProductUrl("https://example.com/product1");
        createAndSendAlertrequest.setDesiredPrice(100.0);

        ProductPrice mockProduct = new ProductPrice("https://example.com/product1", 95.0);
        when(priceAlertUtil.getProductPrices()).thenReturn(List.of(mockProduct));
        when(priceTrackingRepository.findByUserNameAndProductUrl(any(), any())).thenReturn(Collections.emptyList());

        PriceTrackingResponse mockResponse = new PriceTrackingResponse(
                "Mocked response", true, "testuser", "https://example.com/product1", 95.0, 100.0);
        when(priceAlertUtil.createProductPriceAndSendAlert(any(PriceTrackingRequest.class))).thenReturn(mockResponse);

        // Act
        PriceTrackingResponse response = priceTrackingService.trackUserProductPrice(createAndSendAlertrequest);

        // Assert
        assertNotNull(response);
        assertEquals("Mocked response", response.getMessage());
        verify(priceAlertUtil, times(1)).createProductPriceAndSendAlert(any(PriceTrackingRequest.class));
    }


}
