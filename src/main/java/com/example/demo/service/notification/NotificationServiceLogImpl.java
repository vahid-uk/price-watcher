package com.example.demo.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;


@Service("notificationServiceLogOutput")
public class NotificationServiceLogImpl implements NotificationService {

    private final static Logger log = LoggerFactory.getLogger(NotificationServiceLogImpl.class);

    /**
     * This is called by ProductRenewalService when the price has met desired price limit of a given url
     */
    @Override
    public void notifyUser(String url, BigDecimal expectedPrice, BigDecimal price, LinkedHashMap<LocalDateTime, BigDecimal> history) {
        log.atInfo().log("Alert {} has dropped or hit threshold of {}, latest price is {}", url, expectedPrice, price);
        if (!history.isEmpty()) {
            for (Map.Entry<LocalDateTime, BigDecimal> entry : history.entrySet()) {
                log.atInfo().log("History change: " + entry.getKey().format(DateTimeFormatter.ofPattern("yyyy MM dd HH:mm:ss")) + ", had price of: " + entry.getValue());
            }
        }
    }
}
