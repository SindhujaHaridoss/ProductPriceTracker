package com.nhsbsa.productpricealerttracker.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductPrice {
    private String productUrl;
    private double currentPrice;

    public ProductPrice(String productUrl, double currentPrice) {
        this.productUrl = productUrl;
        this.currentPrice = currentPrice;
    }

    public ProductPrice() {
    }

}
