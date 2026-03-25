package com.example.ecommerceproject.exception;

public class ApiException extends RuntimeException{
    private final Integer HttpStatus;

    public ApiException(String message, Integer status){
        super(message);
        this.HttpStatus = status;
    }

    public Integer getStatus(){
        return HttpStatus;
    }
}
