package com.indico.storage;

import java.io.BufferedInputStream;
import java.io.InputStream;
import com.indico.Mutation;

/**
 * Retrieve a blob
 * After setting the parameters to retrieve the blob use getInputStream to retrieve the content of the blob
 */
public class RetrieveBlob {
    private String url;

    public RetrieveBlob url(String url) {
        this.url = url;
        return this;
    }

    /**
     * Initiates the request to retrieve the blob and returns a InputStream
     * @return InputStream
     */
    public InputStream getInputStream() {
        return new BufferedInputStream(null);
    }
}