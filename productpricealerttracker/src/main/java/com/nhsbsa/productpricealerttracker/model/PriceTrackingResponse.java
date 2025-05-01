package com.nhsbsa.productpricealerttracker.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceTrackingResponse {
    private String message;
    private boolean priceMatched;
    private String userName;
    private String productUrl;
    private double droppedPrice;
    private double desiredPrice;

    public PriceTrackingResponse(String message, boolean priceMatched, String userName, String productUrl,
            double droppedPrice, double desiredPrice) {
        this.message = message;
        this.priceMatched = priceMatched;
        this.userName = userName;
        this.productUrl = productUrl;
        this.droppedPrice = droppedPrice;
        this.desiredPrice = desiredPrice;
    }

    public PriceTrackingResponse() {
    }
}
