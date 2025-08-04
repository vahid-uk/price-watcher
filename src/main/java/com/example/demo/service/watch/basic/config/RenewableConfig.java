package com.example.demo.service.watch.basic.config;

import com.example.demo.service.watch.basic.ProductRenewalService;
import com.example.demo.util.RenewableConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

/**
 * This is the configuration bean for RenewableConcurrentHashMap

 */
@ConditionalOnProperty(
              name = "application.renewable-type",
                     havingValue = "default"
             )
@Configuration
public class RenewableConfig {

    @Bean
    public RenewableConcurrentHashMap<String, BigDecimal> productWatchRenewableConcurrentHashMap(
            ProductRenewalService productRenewalService) {
        return new RenewableConcurrentHashMap<>(productRenewalService);
    }

}
