package com.example.demo.service.watch.history;

import com.example.demo.dto.Product;
import com.example.demo.dto.ProductWatchHistory;
import com.example.demo.service.ProductService;
import com.example.demo.service.notification.NotificationService;
import com.example.demo.util.RenewableConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.demo.util.HistoryUtil.checkProductHistory;

@Service
public class ProductRenewalWithHistoryService implements RenewableConcurrentHashMap.RenewalListener<String, ProductWatchHistory> {

    private final static Logger log = LoggerFactory.getLogger(ProductRenewalWithHistoryService.class);

    private final ProductService productService;
    private final NotificationService notificationService;

    public ProductRenewalWithHistoryService(@Qualifier("inMemory") ProductService productService,
                                            @Qualifier("notificationServiceLogOutput") NotificationService notificationService) {
        this.productService = productService;

        this.notificationService = notificationService;
    }

    @Override
    public void onRenew(String url, ProductWatchHistory productWatchHistory) {
        Optional<Product> productOptional = productService.findByUrl(url);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.price() != null) {
                checkProductHistory(productWatchHistory, product.price());
                if (productWatchHistory.getPrice().compareTo(product.price()) >= 0) {

                    log.atInfo().log("Notifying user that {} has reached desired price limit of: {}, " +
                                    "latest price: {} ", product.url() ,
                            productWatchHistory.getPrice(), product.price());

                    notificationService.notifyUser(url, productWatchHistory.getPrice(), product.price(),
                            productWatchHistory.getHistory());

                }
            }
        }
    }
}