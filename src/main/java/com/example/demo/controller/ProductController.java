package com.example.demo.controller;

import com.example.demo.dto.Product;
import com.example.demo.service.ProductService;
import com.example.demo.service.watch.ProductWatchService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.example.demo.service.ProductService.DUMMY_ECOMMERCE_URL;

@RestController
public class ProductController {

    private final ProductService productService;

    private final ProductWatchService productWatchService;

    public ProductController(@Qualifier("inMemory") ProductService productService,
                             ProductWatchService productWatchService) {
        this.productService = productService;
        this.productWatchService = productWatchService;
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") Long id) {
        Optional<Product> productOptional = productService.findById(id);
        return productOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/product")
    public ResponseEntity<Product> getProduct(@RequestBody Product product) {
        if (Optional.ofNullable(product).isEmpty() ||
                ObjectUtils.isEmpty(product.id()) ||
                (
                        !ObjectUtils.isEmpty(product.id()) &&
                                productService.findById(product.id()).isEmpty())
        ) {
            return ResponseEntity.badRequest().build();
        }
        Optional<Product> productOptional = productService.updateProduct(product);
        if (productOptional.isPresent()) {
            productWatchService.addNewPriceToHistory(DUMMY_ECOMMERCE_URL+product.id(), product.price());
            return ResponseEntity.ok(productOptional.get());
        }
        return ResponseEntity.notFound().build();
    }

}
