package com.indico.request;

import com.apollographql.apollo.exception.ApolloException;
import com.indico.IndicoClient;
import com.indico.RestRequest;
import com.indico.JSON;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

public class GraphQLRequest implements RestRequest<JSONObject> {

    private final IndicoClient client;
    private String query;
    private String operationName;
    private JSONObject variables;

    public GraphQLRequest(IndicoClient client) {
        this.client = client;
    }

    public GraphQLRequest query(String query) {
        this.query = query;
        return this;
    }

    public GraphQLRequest operationName(String operationName) {
        this.operationName = operationName;
        return this;
    }

    public GraphQLRequest variables(JSONObject variables) {
        this.variables = variables;
        return this;
    }

    @Override
    public JSONObject call() throws IOException {
        JSONObject json = new JSONObject();
        json.put("query", this.query);
        if (this.operationName != null) {
            json.put("operationName", this.operationName);
        }
        if (this.variables != null) {
            json.put("variables", this.variables);
        }

        String uploadUrl = this.client.config.getAppBaseUrl() + "/graph/api/graphql";
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(uploadUrl)
                .post(requestBody)
                .build();

        Response result = this.client.okHttpClient.newCall(request).execute();

        String stringBody = result.body().string();
        JSONObject response = new JSON(stringBody).asJSONObject();
        result.body().close();

        if (!response.isNull("errors")) {
            JSONArray errors = response.getJSONArray("errors");
            throw new ApolloException(errors.toString());
        }
        JSONObject data = response.getJSONObject("data");
        result.close();
        return data;
    }
}
