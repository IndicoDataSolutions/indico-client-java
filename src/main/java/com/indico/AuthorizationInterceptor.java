package com.indico;

import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

public class AuthorizationInterceptor implements Interceptor{
    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final String refreshToken;
    final String serverURL;
    private String authToken;
    private final IndicoConfig indicoConfig;


    public AuthorizationInterceptor(String serverURL, String refreshToken, IndicoConfig config) {
        this.refreshToken = refreshToken;
        this.serverURL = serverURL;
        this.indicoConfig = config;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Authorization", authHeader())
                .build();

        return chain.proceed(request);
    }

    public String authHeader() {
        return "Bearer " + authToken;
    }
    
    public void refreshAuthState() throws IOException {
        Response refreshResponse = null;
        for(int counter = 0; counter < indicoConfig.maxRetries; counter++) {
            try{
                Call refreshCall = refreshAccessToken(this.serverURL, this.refreshToken);
                 refreshResponse = refreshCall.execute();
            }
            catch(Exception ex) {
                continue;
            }
        }
        if (refreshResponse != null && refreshResponse.code() == 200) {
            String responseBody = refreshResponse.body().string();
            JSONObject json = new JSONObject(responseBody);
            authToken = (String) json.get("auth_token");
        } else {
            throw new RuntimeException("Failed to refresh authentication state");
        }
    }


    private Call refreshAccessToken(String serverURL, String apiToken) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(indicoConfig.connectionReadTimeout, TimeUnit.SECONDS)
                .addInterceptor(new RetryInterceptor(indicoConfig))
                .writeTimeout(indicoConfig.connectionWriteTimeout, TimeUnit.SECONDS)
                .connectTimeout(indicoConfig.connectTimeout, TimeUnit.SECONDS)
                .build();
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
