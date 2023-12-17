package com.example.productservice.customException;

public class EmptyOrderException extends RuntimeException {
    public EmptyOrderException(String message) {
       super(message);
    }
}
