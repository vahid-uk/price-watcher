package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad content")
public class ProductException extends RuntimeException {

    public ProductException() {
     super();
    }
    public ProductException(String message) {
        super(message);
    }
}
