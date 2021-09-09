package com.indico.exceptions;

public class IndicoMutationException extends IndicoBaseException{
    public IndicoMutationException(String msg) {
        super(msg);
    }

    public IndicoMutationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
