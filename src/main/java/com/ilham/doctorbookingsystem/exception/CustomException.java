package com.ilham.doctorbookingsystem.exception;

import com.ilham.doctorbookingsystem.enums.ErrorMessage;

public class CustomException extends RuntimeException{

    public CustomException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
