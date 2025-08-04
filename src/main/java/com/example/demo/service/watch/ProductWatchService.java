package com.example.demo.service.watch;

import java.math.BigDecimal;

public interface ProductWatchService {
    void addEntry(String url, BigDecimal price);

    void removeEntry(String url);

    void addNewPriceToHistory(String url, BigDecimal price);
}
