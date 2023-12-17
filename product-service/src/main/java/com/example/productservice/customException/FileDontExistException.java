package com.example.productservice.customException;

public class FileDontExistException extends RuntimeException {
    public FileDontExistException(String message){
        super(message);
    }
}
