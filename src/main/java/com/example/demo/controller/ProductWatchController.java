package com.example.demo.controller;

import com.example.demo.dto.ProductWatch;
import com.example.demo.service.watch.ProductWatchService;
import com.example.demo.service.watch.basic.ProductWatcherServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
public class ProductWatchController {

    private final static Logger log = LoggerFactory.getLogger(ProductWatchController.class);

    private final ProductWatchService productWatchService;

    public ProductWatchController(ProductWatchService productWatchService) {
        this.productWatchService = productWatchService;
    }

    @PostMapping("/watch")
    public ResponseEntity<ProductWatch> postProduct(@RequestBody ProductWatch productWatch) {
        if (Optional.ofNullable(productWatch).isEmpty() ||
                ObjectUtils.isEmpty(productWatch.price()) ||
                ObjectUtils.isEmpty(productWatch.url())) {
            return ResponseEntity.badRequest().build();
        }
        log.atInfo().log("adding url {} with price of {} ", productWatch.url(), productWatch.price());
        productWatchService.addEntry(productWatch.url(), productWatch.price());
        return ResponseEntity.created(URI.create(productWatch.url())).body(productWatch);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ProductWatch> deleteEntry(@RequestBody ProductWatch productWatch) {
        if (Optional.ofNullable(productWatch).isEmpty() ||
                ObjectUtils.isEmpty(productWatch.url())
        ) {
            return ResponseEntity.badRequest().build();
        }
        log.atInfo().log("removing url {}  ", productWatch.url());
        productWatchService.removeEntry(productWatch.url());
        return ResponseEntity.noContent().build();
    }
}
