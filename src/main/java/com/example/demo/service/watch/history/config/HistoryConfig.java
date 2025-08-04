package com.example.demo.service.watch.history.config;

import com.example.demo.dto.ProductWatchHistory;
import com.example.demo.service.watch.history.ProductRenewalWithHistoryService;
import com.example.demo.util.RenewableConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HistoryConfig {

    @ConditionalOnProperty(
            name = "application.renewable-type",
            havingValue = "history"
    )
    @Bean
    public RenewableConcurrentHashMap<String, ProductWatchHistory> productWatchRenewableWithHistoryConcurrentHashMap(
            ProductRenewalWithHistoryService productRenewalWithHistoryService) {
        return new RenewableConcurrentHashMap<>(productRenewalWithHistoryService);
    }
}
