package com.indico.storage;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import com.indico.IndicoClient;
import com.indico.Mutation;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Retrieve a blob
 * After setting the parameters to retrieve the blob use getInputStream to retrieve the content of the blob
 */
public class RetrieveBlob {
    private String url;
    private IndicoClient client;

    public RetrieveBlob(IndicoClient client) {
        this.client = client;
    }

    public String gzipDecompress(InputStream compressed) throws IOException {
        String uncompressed;
        try (
                GZIPInputStream gis = new GZIPInputStream(compressed);
                Reader reader = new InputStreamReader(gis);
                Writer writer = new StringWriter()
        ) {

            char[] buffer = new char[10240];
            for (int length = 0; (length = reader.read(buffer)) > 0; ) {
                writer.write(buffer, 0, length);
            }
            uncompressed = writer.toString();
        }

        return uncompressed;
    }

    public RetrieveBlob url(String url) {
        url = url.replaceAll("\"", "");
        // Drop gzip
        String path = url.split("://")[1];
        this.url = client.config.getAppBaseUrl() + "/" + path;
        System.out.println(this.url);
        return this;
    }

    private Response retrieveBlob() throws IOException {
        Response response = this.client.okHttpClient.newCall(new Request.Builder().url(this.url).get().build()).execute();
        System.out.println(response.code());
        System.out.println(response.request().url());
        if (response.isSuccessful()) {
            return response;
        } else {
            throw new IOException("Failed to retrieve blob at url " + url);
        }
    }

    private InputStream getInputStream() throws IOException{
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
     * @return Blob
     * @throws IOException
     */
    public Blob execute() throws IOException {
        return new Blob(this.getInputStream());
    }


}