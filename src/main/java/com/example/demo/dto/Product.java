package com.example.demo.dto;

import java.math.BigDecimal;

public record Product(Long id, String title, String url, BigDecimal price) {

}
