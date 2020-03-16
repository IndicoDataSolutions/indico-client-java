package com.indico;

import java.io.IOException;

public interface RestRequest<T> {
    public T call() throws IOException;
}
