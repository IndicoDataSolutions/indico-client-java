package com.indico.exception;

public class IndicoAuthorizationException extends IndicoBaseException{
    public IndicoAuthorizationException(String msg) {
        super(msg);
    }

    public IndicoAuthorizationException(String msg, Throwable cause){
        super(msg, cause);
    }
}
