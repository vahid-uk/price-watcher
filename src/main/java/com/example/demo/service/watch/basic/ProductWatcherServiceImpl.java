package com.example.demo.service.watch.basic;

import com.example.demo.service.watch.ProductWatchService;
import com.example.demo.util.RenewableConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.example.demo.service.ProductService.DUMMY_ECOMMERCE_URL;

@ConditionalOnProperty(
        name = "application.renewable-type",
        havingValue = "default"
)
@Service
public class ProductWatcherServiceImpl implements ProductWatchService {

    private final static Logger log = LoggerFactory.getLogger(ProductWatcherServiceImpl.class);

    private final RenewableConcurrentHashMap<String, BigDecimal> productWatch;

    public ProductWatcherServiceImpl(RenewableConcurrentHashMap<String, BigDecimal> productWatch) {
        this.productWatch = productWatch;
        this.productWatch.put(DUMMY_ECOMMERCE_URL+"1", new BigDecimal("9.99"));
    }

    @Override
    public void addEntry(String url, BigDecimal price) {
        productWatch.put(url,price);
        log.atInfo().log("Entry Added. New Product watch list size {}", productWatch.size());
    }

    @Override
    public void addNewPriceToHistory(String url, BigDecimal price) {
        log.atInfo().log("Entry being checked. Nothing to do in basic implementation");
    }

    @Override
    public void removeEntry(String url) {
        productWatch.remove(url);
        log.atInfo().log("Entry removed. New product watch list size {}", productWatch.size());
    }
}
