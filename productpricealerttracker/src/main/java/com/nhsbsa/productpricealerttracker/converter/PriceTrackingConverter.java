package com.nhsbsa.productpricealerttracker.converter;

import org.springframework.stereotype.Component;

import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;
import com.nhsbsa.productpricealerttracker.model.PriceTrackingRequest;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PriceTrackingConverter {
        public UserPriceTracker toEntity(PriceTrackingRequest request) {
                return UserPriceTracker.builder()
                                .userName(request.getUserName())
                                .productUrl(request.getProductUrl())
                                .desiredPrice(request.getDesiredPrice())
                                .checkFrequency(request.getCheckFrequency())
                                .alertSent(false) // default, since it's a new request
                                .emailId(request.getEmailId())
                                .build();
        }
}
