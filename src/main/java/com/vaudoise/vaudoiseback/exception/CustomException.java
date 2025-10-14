package com.vaudoise.vaudoiseback.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Class that represents its own exception for application errors.
 */
@Getter
public class CustomException extends Exception {

    /**
     * Error identifier
     */
    private final String errorKey;
    /**
     * Default message
     */
    private final String defaultMessage;
    /**
     * HTTP Status
     */
    private final HttpStatus status;

    /**
     * Instance of a new CustomException
     *
     * @param error      error
     */
    public CustomException(ErrorEnum error, HttpStatus status) {
        super(error.getDescription());
        this.errorKey = String.valueOf(error.getCode());
        this.defaultMessage = error.getDescription();
        this.status = status;
    }
}