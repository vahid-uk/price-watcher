package com.example.demo.service.notification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public interface NotificationService {

    void notifyUser(String url, BigDecimal expectedPrice, BigDecimal price,LinkedHashMap<LocalDateTime, BigDecimal> history);

}
