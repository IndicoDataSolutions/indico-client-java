package com.indico.storage;

import com.indico.Mutation;

/**
 * Remove a blob from indico blob storage
 */
public class PurgeBlob implements Mutation<PurgeBlobResult> {
    String url;

    public PurgeBlob url(String url) {
        this.url = url;
        return this;
    }

    public PurgeBlobResult execute() {
        return new PurgeBlobResult();
    }
}