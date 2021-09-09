package com.indico.exceptions;

public class IndicoInvalidRequestException extends IndicoBaseException{
    public IndicoInvalidRequestException(String msg) {
        super(msg);
    }

    public IndicoInvalidRequestException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
