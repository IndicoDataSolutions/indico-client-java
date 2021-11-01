package com.indico.exceptions;

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
