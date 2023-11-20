package com.indico.storage;

import java.io.*;
import java.util.zip.GZIPInputStream;

import com.indico.IndicoKtorClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Retrieve a blob after setting the parameters to retrieve the blob use
 * getInputStream to retrieve the content of the blob
 */
public class RetrieveBlob implements AutoCloseable {

    private String url;
    private IndicoKtorClient client;
    private Response response;

    public RetrieveBlob(IndicoKtorClient client) {
        this.client = client;
    }

    /**
     * Decompress GZip InputStream
     *
     * @param compressed Compressed InputStream
     * @return Decompressed stream as string
     * @throws IOException
     */
    public String gzipDecompress(InputStream compressed) throws IOException {
        String uncompressed;
        try (
                GZIPInputStream gis = new GZIPInputStream(compressed);
                Reader reader = new InputStreamReader(gis);
                Writer writer = new StringWriter()) {

            char[] buffer = new char[10240];
            for (int length = 0; (length = reader.read(buffer)) > 0;) {
                writer.write(buffer, 0, length);
            }
            uncompressed = writer.toString();
        }

        return uncompressed;
    }

    /**
     * Blob storage url
     *
     * @param url Blob url
     * @return RetrieveBlob
     */
    public RetrieveBlob url(String url) {
        url = url.replaceAll("\"", "");
        // Drop gzip
        String path = url.split("://")[1];
        this.url = client.getConfig().getAppBaseUrl() + "/" + path;
        return this;
    }

    private Response retrieveBlob() throws IOException {
        Response response = this.client.getHttpClient().newCall(new Request.Builder().url(this.url).get().build()).execute();
        if (response.isSuccessful()) {
            return response;
        } else {
            throw new IOException("Failed to retrieve blob at url " + url);
        }
    }

    private InputStream getInputStream() throws IOException {
        Response response = this.retrieveBlob();
        InputStream data = response.body().byteStream();
        if (this.url.contains(".gz")) {
            return new GZIPInputStream(data);
        } else {
            return data;
        }
    }

    /**
     * Execute retrieve blob from storage call.
     *
     * @return Blob
     * @throws IOException
     */
    public Blob execute() throws IOException {
        Response response = this.retrieveBlob();
        this.response = response;
        return new Blob(response);
    }

    public void close(){
        this.response.close();
    }

}
