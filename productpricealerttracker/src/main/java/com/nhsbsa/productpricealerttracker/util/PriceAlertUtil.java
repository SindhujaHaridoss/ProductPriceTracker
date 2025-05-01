package com.nhsbsa.productpricealerttracker.util;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.nhsbsa.productpricealerttracker.config.LoadJsonConfig;
import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingRequest;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingResponse;
import com.nhsbsa.productpricealerttracker.model.ProductPrice;
import com.nhsbsa.productpricealerttracker.notificationservice.EmailNotificationService;
import com.nhsbsa.productpricealerttracker.repository.PriceTrackingRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PriceAlertUtil {

    private final EmailNotificationService emailNotificationService;
    private final PriceTrackingRepository priceTrackingRepository;
    private static final Random RANDOM = new Random();

    public PriceAlertUtil(EmailNotificationService emailNotificationService,
            PriceTrackingRepository priceTrackingRepository, LoadJsonConfig loadJsonConfig) {
        this.emailNotificationService = emailNotificationService;
        this.priceTrackingRepository = priceTrackingRepository;
        this.productPrices = loadJsonConfig.productPrices();
    }

    private final List<ProductPrice> productPrices;

    public List<ProductPrice> getProductPrices() {
        return productPrices;
    }

    public void processProductPriceAndSendAlert(UserPriceTracker request) {
        log.info("Process product price track requests");
        List<ProductPrice> actualProductPrices = getProductPrices();
        List<ProductPrice> droppedPriceList = generateRandomPriceList(actualProductPrices);
        droppedPriceList.stream()
                .filter(p -> p.getProductUrl().equals(request.getProductUrl()))
                .filter(p -> p.getCurrentPrice() <= request.getDesiredPrice())
                .findFirst()
                .ifPresent(p -> {
                    String message = buildAlertMessage(request, p);
                    log.info(message);
                    emailNotificationService.sendNotification(request, message);
                    request.setAlertSent(true);
                    request.setDroppedPrice(p.getCurrentPrice());
                    priceTrackingRepository.save(request);
                });
    }

    public PriceTrackingResponse checkPriceAndSendAlert(UserPriceTracker userPriceTracker,
            PriceTrackingRequest request) {
        Optional<ProductPrice> matchedProduct = productPrices.stream()
                .filter(p -> p.getProductUrl().equals(userPriceTracker.getProductUrl()))
                .filter(p -> p.getCurrentPrice() <= userPriceTracker.getDesiredPrice())
                .findFirst();
        if (matchedProduct.isPresent()) {
            log.info("Product and price condition met");
            String message = buildAlertMessage(userPriceTracker, matchedProduct.get());
            log.info(message);
            emailNotificationService.sendNotification(userPriceTracker, message);
            userPriceTracker.setAlertSent(true);
            userPriceTracker.setDroppedPrice(matchedProduct.get().getCurrentPrice());
            priceTrackingRepository.save(userPriceTracker);
            return new PriceTrackingResponse(
                    "Price dropped and the alert has sent to the registered email",
                    true,
                    request.getUserName(),
                    request.getProductUrl(), 0.00, request.getDesiredPrice());
        } else {
            log.info("price not dropped");
            userPriceTracker.setDesiredPrice(request.getDesiredPrice());
            priceTrackingRepository.save(userPriceTracker);
            return new PriceTrackingResponse(
                    "Price tracker updated successfully and alert will be sent to the registered email when price drops",
                    false,
                    request.getUserName(),
                    request.getProductUrl(), 0.00, request.getDesiredPrice());
        }
    }

    public PriceTrackingResponse createProductPriceAndSendAlert(PriceTrackingRequest request) {
        log.info("create new request");
        Optional<ProductPrice> matchedProduct = productPrices.stream()
                .filter(p -> p.getProductUrl().equals(request.getProductUrl()))
                .filter(p -> p.getCurrentPrice() <= request.getDesiredPrice())
                .findFirst();

        if (matchedProduct.isPresent()) {
            ProductPrice p = matchedProduct.get();
            UserPriceTracker productPriceTrackRequest = new UserPriceTracker();
            productPriceTrackRequest.setUserName(request.getUserName().toLowerCase());
            productPriceTrackRequest.setProductUrl(request.getProductUrl());
            productPriceTrackRequest.setDesiredPrice(request.getDesiredPrice());
            productPriceTrackRequest.setCheckFrequency(request.getCheckFrequency());
            productPriceTrackRequest.setEmailId(request.getEmailId().toLowerCase());
            productPriceTrackRequest.setDroppedPrice(p.getCurrentPrice());
            productPriceTrackRequest.setAlertSent(true);
            String message = buildAlertMessage(productPriceTrackRequest, p);
            log.info(message);
            emailNotificationService.sendNotification(productPriceTrackRequest, message);
            priceTrackingRepository.save(productPriceTrackRequest);
            return new PriceTrackingResponse(
                    "Price drop detected — we've sent an alert to your registered email",
                    true,
                    request.getUserName(),
                    request.getProductUrl(), p.getCurrentPrice(), request.getDesiredPrice());
        } else {
            log.info("No price drop for user {} on product {} yet.",
                    request.getUserName(), request.getProductUrl());
            UserPriceTracker productPriceTrackRequest = new UserPriceTracker();
            productPriceTrackRequest.setUserName(request.getUserName().toLowerCase());
            productPriceTrackRequest.setProductUrl(request.getProductUrl());
            productPriceTrackRequest.setDesiredPrice(request.getDesiredPrice());
            productPriceTrackRequest.setCheckFrequency(request.getCheckFrequency());
            productPriceTrackRequest.setEmailId(request.getEmailId().toLowerCase());
            productPriceTrackRequest.setAlertSent(false);
            priceTrackingRepository.save(productPriceTrackRequest);
            log.info("Saved successfully");
            return new PriceTrackingResponse(
                    "You've successfully registered for price tracking. An alert will be sent to your email when the price drops.",
                    false,
                    request.getUserName(),
                    request.getProductUrl(), 0.00, request.getDesiredPrice());
        }
    }

    private String buildAlertMessage(UserPriceTracker request, ProductPrice productPrice) {
        return String.format(
                "Alert! User %s: Price dropped for %s - Current: %.2f, Desired: %.2f",
                request.getUserName(),
                productPrice.getProductUrl(),
                productPrice.getCurrentPrice(),
                request.getDesiredPrice());
    }

    public static List<ProductPrice> generateRandomPriceList(List<ProductPrice> actualPriceList) {
        log.info("Generating random price list");
        return actualPriceList.stream()
                .map(product -> {
                    ProductPrice newProduct = new ProductPrice();
                    newProduct.setProductUrl(product.getProductUrl());

                    double basePrice = product.getCurrentPrice();
                    double fluctuation = (RANDOM.nextDouble() * 0.2) - 0.1; // -10% to +10%
                    double newPrice = basePrice + (basePrice * fluctuation);

                    newProduct.setCurrentPrice(Math.round(newPrice * 100.0) / 100.0); // round to 2 decimals
                    return newProduct;
                })
                .toList();
    }
}
