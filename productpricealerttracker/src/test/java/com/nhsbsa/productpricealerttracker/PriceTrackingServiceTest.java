package com.nhsbsa.productpricealerttracker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.nhsbsa.productpricealerttracker.controller.PriceTrackingController;
import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;
import com.nhsbsa.productpricealerttracker.enums.Frequency;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingRequest;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingResponse;
import com.nhsbsa.productpricealerttracker.model.ProductPrice;
import com.nhsbsa.productpricealerttracker.notificationservice.EmailNotificationService;
import com.nhsbsa.productpricealerttracker.repository.PriceTrackingRepository;
import com.nhsbsa.productpricealerttracker.scheduler.PriceCheckScheduler;
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
   


    @Autowired
    private PriceCheckScheduler priceCheckScheduler;
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
        priceCheckScheduler = new PriceCheckScheduler(priceTrackingRepository, priceAlertUtil);
        
    }


@Test
    void testTrackUserProductPrice_NewRecord() {
        PriceTrackingRequest request = new PriceTrackingRequest("testuser", "https://example.com/product1", 100.00, Frequency.MORNING_ONLY, "abc@sdf.com");
        ProductPrice productPrice = new ProductPrice("https://example.com/product1", 90.00);
        
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

        PriceTrackingResponse response = priceTrackingService.trackUserProductPrice(createAndSendAlertrequest);

        assertNotNull(response);
        assertEquals("Mocked response", response.getMessage());
        verify(priceAlertUtil, times(1)).createProductPriceAndSendAlert(any(PriceTrackingRequest.class));
    }

    @Test
    void testProcessMorningAlertRequests_shouldProcessOnlyUnsentAlerts() {
        UserPriceTracker tracker1 = new UserPriceTracker(1L, "user1", "url1", 100.0, Frequency.MORNING_ONLY, false, "test@example.com", 0.0);
        UserPriceTracker tracker2 = new UserPriceTracker(2L, "user2", "url2", 120.0, Frequency.MORNING_ONLY, true, "test2@example.com", 0.0);

        List<UserPriceTracker> mockList = Arrays.asList(tracker1, tracker2);

        when(priceTrackingRepository.findByCheckFrequency(Frequency.MORNING_ONLY)).thenReturn(mockList);

        priceCheckScheduler.processMorningAlertRequests();

        verify(priceAlertUtil, times(1)).processProductPriceAndSendAlert(tracker1);
        verify(priceAlertUtil, never()).processProductPriceAndSendAlert(tracker2);
    }

    @Test
    void testProcess24HoursAlertRequests_shouldProcessUnsentAlerts() {
        UserPriceTracker trackerRecord = new UserPriceTracker(4L, "user4", "http://example.com/product", 150.0, Frequency.MIDNIGHT_ONLY, false, "24@example.com", 0.0);

        when(priceTrackingRepository.findByCheckFrequency(Frequency.MIDNIGHT_ONLY))
                .thenReturn(Collections.singletonList(trackerRecord));

        priceCheckScheduler.process24hoursAlertRequests();

        verify(priceAlertUtil, times(1)).processProductPriceAndSendAlert(trackerRecord);
    }
    @Test
    void testProcessMidnightAlertRequests_shouldProcessUnsentAlerts() {
        UserPriceTracker trackerRecord = new UserPriceTracker(3L, "user3", "http://example.com/product1", 90.0, Frequency.MIDNIGHT_ONLY, false, "mid@example.com", 0.0);

        when(priceTrackingRepository.findByCheckFrequency(Frequency.MIDNIGHT_ONLY))
                .thenReturn(Collections.singletonList(trackerRecord));

        priceCheckScheduler.processMidnightAlertRequests();

        verify(priceAlertUtil, times(1)).processProductPriceAndSendAlert(trackerRecord);
    }
}