package com.nhsbsa.productpricealerttracker.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;
import com.nhsbsa.productpricealerttracker.enums.Frequency;
import com.nhsbsa.productpricealerttracker.repository.PriceTrackingRepository;
import com.nhsbsa.productpricealerttracker.util.PriceAlertUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PriceCheckScheduler {

    private final PriceTrackingRepository priceTrackingRepository;
    private final PriceAlertUtil priceAlertUtil;

    public PriceCheckScheduler(PriceTrackingRepository priceTrackingRepository, PriceAlertUtil priceAlertUtil) {
        this.priceTrackingRepository = priceTrackingRepository;
        this.priceAlertUtil = priceAlertUtil;
    }
    // Run every hour (cron: second, minute, hour, day, month, weekday)

    @Scheduled(cron = "0 0 6 * * *")
    public void processMorningAlertRequests() {
        log.info("Morning scdeuler starts");
        List<UserPriceTracker> morningTrackRequests = priceTrackingRepository
                .findByCheckFrequency(Frequency.MORNING_ONLY);
        morningTrackRequests.stream()
                .filter(morningtrack -> !morningtrack.isAlertSent())
                .forEach(priceAlertUtil::processProductPriceAndSendAlert);
    }

    // @Scheduled(cron = "0 0 0 * * *")
    @Scheduled(cron = "0 0 0 * * *")
    public void processMidnightAlertRequests() {
        log.info("Midnight sheduler starts");
        List<UserPriceTracker> midnightTrackRequests = priceTrackingRepository
                .findByCheckFrequency(Frequency.MIDNIGHT_ONLY);
        log.info("count {}", midnightTrackRequests.size());
        midnightTrackRequests.stream()
                .filter(morningtrack -> !morningtrack.isAlertSent())
                .forEach(priceAlertUtil::processProductPriceAndSendAlert);
    }

        @Scheduled(fixedRate = 86400000)
        public void process24hoursAlertRequests() {
        log.info("24 hours sheduler starts");
        List<UserPriceTracker> hours24TrackRequests = priceTrackingRepository
                .findByCheckFrequency(Frequency.MIDNIGHT_ONLY);

        log.info("count {}", hours24TrackRequests.size());
        hours24TrackRequests.stream()
                .filter(morningtrack -> !morningtrack.isAlertSent())
                .forEach(priceAlertUtil::processProductPriceAndSendAlert);
    }
}
