package com.example.demo.service.watch.history;

import com.example.demo.dto.ProductWatchHistory;
import com.example.demo.service.watch.ProductWatchService;
import com.example.demo.util.RenewableConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.example.demo.util.HistoryUtil.checkProductHistory;


@ConditionalOnProperty(
        name = "application.renewable-type",
        havingValue = "history"
)
@Service
public class ProductWatcherWithHistoryServiceImpl implements ProductWatchService {

    private final static Logger log = LoggerFactory.getLogger(ProductWatcherWithHistoryServiceImpl.class);

    private final RenewableConcurrentHashMap<String, ProductWatchHistory> productWatch;

    public ProductWatcherWithHistoryServiceImpl(RenewableConcurrentHashMap<String, ProductWatchHistory> productWatch) {
        this.productWatch = productWatch;
        ProductWatchHistory productWatchHistory = new ProductWatchHistory();
        productWatchHistory.setPrice(new BigDecimal("9.99"));
        this.productWatch.put( "http://localhost:8080/product/1", productWatchHistory);
    }

    @Override
    public void addEntry(String url, BigDecimal price) {
        ProductWatchHistory productWatchHistory = new ProductWatchHistory();
        productWatchHistory.setPrice(price);
        productWatch.put(url,productWatchHistory);
        log.atInfo().log("Entry Added. New Product watch list size {}", productWatch.size());
    }

    @Override
    public void addNewPriceToHistory(String url, BigDecimal price) {
        log.atInfo().log("Working on url {}", url);
        ProductWatchHistory productWatchHistory = productWatch.get(url);
        if (productWatchHistory != null) {
            checkProductHistory(productWatchHistory, price);
        }
    }

    @Override
    public void removeEntry(String url) {
        productWatch.remove(url);
        log.atInfo().log("Entry removed. New product watch list size {}", productWatch.size());
    }
}
