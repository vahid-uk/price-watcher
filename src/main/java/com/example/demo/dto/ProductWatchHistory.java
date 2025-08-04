package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class ProductWatchHistory {

    BigDecimal price;


    // using LinkedHashMap To preserve order, although with key being localDateTime sorting after would be easy
    LinkedHashMap<LocalDateTime, BigDecimal> history = new LinkedHashMap<>();

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LinkedHashMap<LocalDateTime, BigDecimal> getHistory() {
        return history;
    }

    public void setHistory(LinkedHashMap<LocalDateTime, BigDecimal> history) {
        this.history = history;
    }

    public void addHistory(LocalDateTime dateTime, BigDecimal price) {
        this.history.put(dateTime,price);
    }
}
