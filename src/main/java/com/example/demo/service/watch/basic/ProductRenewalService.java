package com.example.demo.service.watch.basic;

import com.example.demo.dto.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.notification.NotificationService;
import com.example.demo.util.RenewableConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Optional;


@Service
public class ProductRenewalService implements RenewableConcurrentHashMap.RenewalListener<String, BigDecimal> {

    private final static Logger log = LoggerFactory.getLogger(ProductRenewalService.class);

    private final ProductService productService;

    private final NotificationService notificationService;

    public ProductRenewalService(@Qualifier("inMemory") ProductService productService,
                                 @Qualifier("notificationServiceLogOutput") NotificationService notificationService) {
        this.productService = productService;
        this.notificationService = notificationService;
    }

    /**
     * This overrides RenewableConcurrentHashMap.RenewalListener and carries out customised process
     * This is in effect called internally by renewEntries method from within RenewableConcurrentHashMap.
     * Controlled by a cron schedule that is configured by `cache.renewal.cron` defined in application.properties.
     * If product price is equal to or dropped below desired price then notification process is triggered
     */
    @Override
    public void onRenew(String url, BigDecimal desiredPrice) {
        log.atDebug().log("Working on {}", url);
        Optional<Product> productOptional = productService.findByUrl(url);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.price() != null && desiredPrice.compareTo(product.price()) >= 0 ) {

                log.atInfo().log("Notifying user that {} has reached desired price limit of: {}, " +
                                "latest price: {} ", product.url() , desiredPrice, product.price());

                notificationService.notifyUser(url, desiredPrice, product.price(), new LinkedHashMap<>());
            }
        }
    }
}