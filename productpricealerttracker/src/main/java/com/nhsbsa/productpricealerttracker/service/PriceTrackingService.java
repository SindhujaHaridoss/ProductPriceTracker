package com.nhsbsa.productpricealerttracker.service;

import java.util.List;
import org.springframework.stereotype.Service;

import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;
import com.nhsbsa.productpricealerttracker.exception.InvalidDataException;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingRequest;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingResponse;
import com.nhsbsa.productpricealerttracker.model.ProductPrice;
import com.nhsbsa.productpricealerttracker.repository.PriceTrackingRepository;
import com.nhsbsa.productpricealerttracker.util.PriceAlertUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PriceTrackingService {

    private final PriceTrackingRepository priceTrackingRepository;
    private final PriceAlertUtil priceAlertUtil;

    public PriceTrackingService(PriceTrackingRepository priceTrackingRepository, PriceAlertUtil priceAlertUtil) {
        this.priceTrackingRepository = priceTrackingRepository;
        this.priceAlertUtil = priceAlertUtil;
    }

    /**
     * @param request
     * @return
     */
    public PriceTrackingResponse trackUserProductPrice(PriceTrackingRequest request) {
        List<ProductPrice> productPrices = priceAlertUtil.getProductPrices();
        productPrices.stream()
                .filter(p -> p.getProductUrl().equals(request.getProductUrl()))
                .findFirst()
                .orElseThrow(() -> new InvalidDataException("Product url not listed in tracker. Please enter valid url",
                        "104"));

        List<UserPriceTracker> userPriceRecords = priceTrackingRepository.findByUserNameAndProductUrl(
                request.getUserName(),
                request.getProductUrl());

        boolean noPendingAlerts = userPriceRecords.stream()
                .noneMatch(userPriceTrackRequest -> !userPriceTrackRequest.isAlertSent());

        if (userPriceRecords.isEmpty() || noPendingAlerts) {
            log.info("Create new record for Product {} for the user {} ",
                    request.getProductUrl(), request.getUserName());
            // Creating new record
            return priceAlertUtil.createProductPriceAndSendAlert(request);
        } else {
            log.info("Process records where alert is not sent");
            return (PriceTrackingResponse) userPriceRecords.stream()
                    .map(req -> priceAlertUtil.checkPriceAndSendAlert(req, request)).toList();
        }

    }

    public List<UserPriceTracker> getAllTrackingRecords() {
        return priceTrackingRepository.findAll();
    }
}
