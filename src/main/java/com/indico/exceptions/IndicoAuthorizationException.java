package com.indico.exceptions;

public class IndicoAuthorizationException extends IndicoBaseException{
    public IndicoAuthorizationException(String msg) {
        super(msg);
    }

    public IndicoAuthorizationException(String msg, Throwable cause){
        super(msg, cause);
    }
}
