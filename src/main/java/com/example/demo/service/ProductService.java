package com.example.demo.service;

import com.example.demo.dto.Product;

import java.util.Optional;

public interface ProductService {

    String DUMMY_ECOMMERCE_URL="http://amazing-of-amazonia.com/product/";

    Optional<Product> findById(Long id);

    Optional<Product> findByUrl(String url);

    Optional<Product> updateProduct(Product product);
}
