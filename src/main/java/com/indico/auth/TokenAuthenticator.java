package com.indico.auth;

import java.io.IOException;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import org.json.JSONObject;

public class TokenAuthenticator implements Authenticator {

    private final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    final String refreshToken;
    final String serverURL;

    /**
     * Class Constructor
     *
     * @param serverURL url of graphql server
     * @param refreshToken long term api token from indico_api_token.txt
     */
    public TokenAuthenticator(String serverURL, String refreshToken) {
        this.serverURL = serverURL;
        this.refreshToken = refreshToken;
    }

    /**
     * Gets called when server returns 401 error and refreshes the short term
     * token with refresh token for OkHttpClient
     *
     * @param route
     * @param response
     * @return Request Object with new Authorization Header
     * @throws IOException
     */
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (response.code() == 401) {
            Call refreshCall = refreshAccessToken(this.serverURL, this.refreshToken);
            Response refreshResponse = refreshCall.execute();
            if (refreshResponse != null && refreshResponse.code() == 200) {
                String responseBody = refreshResponse.body().string();
                JSONObject json = new JSONObject(responseBody);
                String authToken = (String) json.get("auth_token");
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + authToken)
                        .build();
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Generates Call for fetching short term access token
     *
     * @param serverURL
     * @param apiToken
     * @return
     * @throws IOException
     */
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
