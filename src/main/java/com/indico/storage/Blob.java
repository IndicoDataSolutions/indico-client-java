package com.indico.storage;

import okhttp3.Response;
import org.json.JSONObject;

import java.io.*;

public class Blob {
    InputStream data = null;

    public Blob(InputStream data) {
        this.data = data;
    }

    public Blob(Response response) {
        this.data = response.body().byteStream();
    }

    public InputStream asInputStream() {
        return this.data;
    }

    public String asString() throws IOException {
        Reader reader = new InputStreamReader(this.data);
        Writer writer = new StringWriter();

        char[] buffer = new char[10240];
        for (int length = 0; (length = reader.read(buffer)) > 0; ) {
            writer.write(buffer, 0, length);
        }
        return writer.toString();
    }

    public JSONObject asJSONObject() throws IOException {
        String jsonString = this.asString();
        return new JSONObject(jsonString);
    }

    public JSONObject asJSONArray() throws IOException{
        String jsonString = this.asString();
        return new JSONObject(jsonString);
    }
}
