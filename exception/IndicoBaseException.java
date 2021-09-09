package com.indico.exception;

public abstract class IndicoBaseException extends RuntimeException {

    public IndicoBaseException(String msg)
    {
        super(msg);
    }

    public IndicoBaseException(String msg, Throwable cause)
    {
       super(msg, cause);
    }
}
