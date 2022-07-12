package com.chenjiayan.reggie.common;

import java.util.concurrent.TimeoutException;

public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
