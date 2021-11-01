package com.indico.exceptions;

public class IndicoNetworkException extends IndicoBaseException{
    public IndicoNetworkException(String msg) {
        super(msg);
    }

    public IndicoNetworkException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
