package com.example.demo.service.notification;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Service("notificationServiceSendWebsocket")
public class NotificationServiceWebSocketImpl implements NotificationService {

    /**
     * This would use websockets to send content to end user.
     */
    @Override
    public void notifyUser(String url, BigDecimal expectedPrice, BigDecimal price, LinkedHashMap<LocalDateTime, BigDecimal> history) {
        // TODO
    }
}
