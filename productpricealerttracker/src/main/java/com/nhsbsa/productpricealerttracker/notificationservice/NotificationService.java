package com.nhsbsa.productpricealerttracker.notificationservice;

import com.nhsbsa.productpricealerttracker.entity.UserPriceTracker;

public interface NotificationService {
    void sendNotification(UserPriceTracker userPriceTracker, String message);
}
