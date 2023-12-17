package com.example.productservice.customException;

public class FileStorageException extends RuntimeException {
    public FileStorageException(String message) {
        super(message);
    }

}
