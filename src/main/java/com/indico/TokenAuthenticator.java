package com.indico;

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

class TokenAuthenticator implements Authenticator {

    final AuthorizationInterceptor interceptor;
    /**
     * Class Constructor
     *
     * @param interceptor Authorization interceptor to update
     */
    public TokenAuthenticator(AuthorizationInterceptor interceptor) {
        this.interceptor = interceptor;
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
        interceptor.refreshAuthState();
        return response.request().newBuilder().header("Authorization", interceptor.authHeader()).build();
    }
}
