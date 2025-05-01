package com.nhsbsa.productpricealerttracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;
import com.nhsbsa.productpricealerttracker.enums.Frequency;

public interface PriceTrackingRepository extends JpaRepository<UserPriceTracker, Long> {

    List<UserPriceTracker> findByUserName(String userName);

    List<UserPriceTracker> findByUserNameAndProductUrl(String userName, String productUrl);

    List<UserPriceTracker> findByCheckFrequency(Frequency morningOnly);
}
