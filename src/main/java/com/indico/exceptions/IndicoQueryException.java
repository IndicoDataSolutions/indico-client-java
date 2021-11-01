package com.indico.exceptions;

public class IndicoQueryException extends IndicoBaseException{
    public IndicoQueryException(String msg) {
        super(msg);
    }

    public IndicoQueryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
