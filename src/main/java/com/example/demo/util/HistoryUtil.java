package com.example.demo.util;

import com.example.demo.dto.ProductWatchHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HistoryUtil {
    private static final Logger log = LoggerFactory.getLogger(HistoryUtil.class);

    public static void checkProductHistory(ProductWatchHistory productWatchHistory, BigDecimal price) {

        if (productWatchHistory.getHistory().isEmpty() || productWatchHistory.getHistory().entrySet().stream()
                .filter(f -> f.getValue().compareTo(price) == 0)
                .findAny()
                .isEmpty()) {
            LocalDateTime localDateTime = LocalDateTime.now();
            log.atInfo().log("Price of item has changed adding new price to history {} {} ",localDateTime, price);
            productWatchHistory.addHistory(localDateTime, price);
        }
    }
}
