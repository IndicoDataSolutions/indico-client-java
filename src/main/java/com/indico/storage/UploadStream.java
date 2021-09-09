package com.indico.storage;

import com.indico.IndicoClient;
import com.indico.JSON;
import com.indico.RestRequest;
import okhttp3.*;
import org.json.JSONArray;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Map;
import java.util.UUID;

public class UploadStream implements RestRequest<JSONArray> {

    private IndicoClient client;
    private Map<String,byte[]> bytes;
    private String fileName;

    public UploadStream(IndicoClient client) {
        this.client = client;
    }

    /**
     * A stream to upload.
     *
     * @param stream Dictionary of string identifiers (ideally file names) to byte[] streams
     * @return UploadFile
     */
    public UploadStream byteStream(Map<String,byte[]> stream) {
        if(stream != null){
            this.bytes = stream;
        }
        return this;
    }

    /**
     * Upload files and return metadata
     *
     * @return JSONArray
     * @throws IOException
     */
    @Override
    public JSONArray call() throws IOException {
        String uploadUrl = this.client.config.getAppBaseUrl() + "/storage/files/store";

        MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if(this.fileName == null || this.fileName.isEmpty()){
            this.fileName = UUID.randomUUID().toString();
        }
        for (String key : this.bytes.keySet()) {
            multipartBody.addFormDataPart(key, key,
                    RequestBody.create(MediaType.parse("application/octet-stream"), this.bytes.get(key)));
        }
        MultipartBody requestBody = multipartBody.build();
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build();

        Response result = client.okHttpClient.newCall(request).execute();
        String body = result.body().string();
        JSONArray fileMeta = new JSON(body).asJSONArray();
        result.close();
        return (JSONArray) fileMeta;
    }
}
