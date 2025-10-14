package com.vaudoise.vaudoiseback.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum of custom errors
 */
@Getter
@RequiredArgsConstructor
public enum ErrorEnum {

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>>>> AUTHENTICATION (1000 - 1099)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    // >>>>>> CLIENTS (1100 - 1199)
    // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    CLIENT_NOT_FOUND(1100, "Client not found"),
    CLIENT_LIST(1101, "Cannot list clients"),
    CLIENT_READ(1102, "Cannot read the client"),
    CLIENT_CREATE(1103, "Cannot create the client"),
    CLIENT_UPDATE(1104, "Cannot update the client"),
    CLIENT_DELETE(1105, "Cannot delete the client"),
    CLIENT_VALIDATION(1106, "Client parameters are not valid"),;

    /**
     * Error identification code
     * -- GETTER --
     * Obtain the code associated with the error
     */
    @Getter
    private final int code;

    /**
     * Error description
     * -- GETTER --
     * Get error description
     */
    private final String description;
}
