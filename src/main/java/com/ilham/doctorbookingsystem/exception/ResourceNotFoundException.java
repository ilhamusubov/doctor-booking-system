package com.ilham.doctorbookingsystem.exception;

import com.ilham.doctorbookingsystem.enums.ErrorMessage;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
    }
}
