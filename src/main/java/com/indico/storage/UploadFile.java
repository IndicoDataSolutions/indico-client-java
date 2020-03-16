package com.indico.storage;

import com.indico.IndicoClient;
import com.indico.RestRequest;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UploadFile implements RestRequest<JSONArray> {
    private IndicoClient client;
    private List<File> files = new ArrayList<>();

    public UploadFile(IndicoClient client) {
        this.client = client;
    }

    public UploadFile filePaths(List<String> filePaths) {
        for (String path : filePaths) {
            File file = new File(path);
            if (file.exists()) {
                files.add(file);
            } else {
                throw new RuntimeException("File " + path + " does not exist");
            }

        }

        return this;
    }
    public JSONArray call() throws IOException {
        String uploadUrl = this.client.config.getAppBaseUrl() + "/storage/files/store";

        MultipartBody.Builder multipartBody = new MultipartBody.Builder().setType(MultipartBody.FORM);

        for(File file : files) {
            multipartBody.addFormDataPart("file", file.getName(),
                    RequestBody.create(MediaType.parse("application/octet-stream"), file));
        }

        MultipartBody requestBody = multipartBody.build();
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build();


        Response result = client.okHttpClient.newCall(request).execute();
        String body = result.body().string();
        JSONArray fileMeta = new JSONArray(body);
        return (JSONArray)fileMeta;
    }
}


