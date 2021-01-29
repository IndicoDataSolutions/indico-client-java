package com.indico;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;

public class AuthorizationInterceptor implements Interceptor{
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final String refreshToken;
    final String serverURL;
    private String authToken;


    public AuthorizationInterceptor(String serverURL, String refreshToken) {
        this.refreshToken = refreshToken;
        this.serverURL = serverURL;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

        return chain.proceed(request);
    }

    public void refreshAuthState() throws IOException {
        Call refreshCall = refreshAccessToken(this.serverURL, this.refreshToken);
        Response refreshResponse = refreshCall.execute();
        if (refreshResponse != null && refreshResponse.code() == 200) {
            String responseBody = refreshResponse.body().string();
            JSONObject json = new JSONObject(responseBody);
            authToken = (String) json.get("auth_token");
        } else {
            throw new RuntimeException("Failed to refresh authentication state");
        }
    }


    private Call refreshAccessToken(String serverURL, String apiToken) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient();
        String refreshTokenURL = serverURL + "/auth/users/refresh_token";
        RequestBody requestBody = RequestBody.create(JSON, "{}");

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + apiToken)
                .url(refreshTokenURL)
                .post(requestBody)
                .build();

        return okHttpClient.newCall(request);
    }
}
