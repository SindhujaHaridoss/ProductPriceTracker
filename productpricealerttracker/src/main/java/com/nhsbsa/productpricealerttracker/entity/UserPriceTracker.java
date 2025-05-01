package com.nhsbsa.productpricealerttracker.entity;

import com.nhsbsa.productpricealerttracker.enums.Frequency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_price_tracker")
@Data
@NoArgsConstructor
@Builder
public class UserPriceTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userPriceTrackId;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String productUrl;
    @Column(nullable = false)
    private double desiredPrice;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Frequency checkFrequency;
    private boolean alertSent;
    @Column(nullable = false)
    private String emailId;
    private double droppedPrice;
    public UserPriceTracker(Long userPriceTrackId, String userName, String productUrl, double desiredPrice,
            Frequency checkFrequency, boolean alertSent, String emailId, double droppedPrice) {
        this.userPriceTrackId = userPriceTrackId;
        this.userName = userName;
        this.productUrl = productUrl;
        this.desiredPrice = desiredPrice;
        this.checkFrequency = checkFrequency;
        this.alertSent = alertSent;
        this.emailId = emailId;
        this.droppedPrice = droppedPrice;
    }
}
