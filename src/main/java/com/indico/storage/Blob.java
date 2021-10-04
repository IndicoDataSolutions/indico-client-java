package com.indico.storage;

import com.indico.JSON;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;

public class Blob implements AutoCloseable {

    InputStream data = null;
    private Response response = null;

    public Blob(InputStream data) {
        this.data = data;
    }

    public Blob(Response response) {
        this.response = response;
        this.data = response.body().byteStream();
    }

    /**
     * Returns Blob as InputStream
     *
     * @return InputStream
     */
    public InputStream asInputStream() {
        return this.data;
    }

    /**
     * Returns Blob as String
     *
     * @return String
     * @throws RuntimeException
     */
    public String asString(){
        try{
        Reader reader = new InputStreamReader(this.data);
        Writer writer = new StringWriter();

        char[] buffer = new char[10240];
        for (int length = 0; (length = reader.read(buffer)) > 0;) {
            writer.write(buffer, 0, length);
        }
        if(response != null){
            response.close();
        }
        return writer.toString();
        } catch(IOException ex){
            if(response != null){
                response.close();
            }
            throw new RuntimeException(ex);
        }

    }

    /**
     * Returns Blob as JSONObject
     *
     * @return JSONObject
     */
    public JSONObject asJSONObject() {
        String jsonString = this.asString();
        return new JSON(jsonString).asJSONObject();
    }

    /**
     * Returns Blob as JSONArray
     *
     * @return JSONArray
     */
    public JSONArray asJSONArray() {
        String jsonString = this.asString();
        return new JSON(jsonString).asJSONArray();
    }

    @Override
    public void close() throws RuntimeException {
        this.response.close();
    }
}
