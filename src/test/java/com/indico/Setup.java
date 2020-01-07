package com.indico;

import com.indico.config.IndicoConfig;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.HttpRequest;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

public class Setup {

    public final int port;

    public Setup(int port) {
        this.port = port;
    }

    public ClientAndServer getMockServer() {
        ClientAndServer mockServer = ClientAndServer.startClientAndServer(this.port);
        mockServer.when(getRequest("LoadMutation")).respond(
                response().withBody(this.getLoadResponse().toString())
        );
        mockServer.when(getRequest("InfoQuery")).respond(
                response().withBody(this.getModelResponse().toString())
        );
        mockServer.when(getRequest("SelectedModelQuery")).respond(
                response().withBody(this.getModelResponse().toString())
        );
        mockServer.when(getRequest("PredictMutation")).respond(
                response().withBody(this.getPredictResponse().toString())
        );
        mockServer.when(getRequest("PdfExtractionMutation")).respond(
                response().withBody(this.getPdfExtractionResponse().toString())
        );
        mockServer.when(getRequest("JobStatusQuery")).respond(
                response().withBody(this.getJobResponse().toString())
        );
        mockServer.when(getRequest("JobWaitQuery")).respond(
                response().withBody(this.getJobResponse().toString())
        );
        mockServer.when(getRequest("JobResultQuery")).respond(
                response().withBody(this.getJobResponse().toString())
        );
        mockServer.when(request("/graph/api/graphql")
                .withMethod("POST")
        ).respond(
                response().withStatusCode(401)
        );
        mockServer.when(request("/auth/users/refresh_token")
                .withMethod("POST")
                .withHeader("Authorization", "Bearer DemoApiToken")
        ).respond(
                response().withBody("{\"auth_token\" : \"DemoRefreshToken\"}")
        );
        return mockServer;
    }

    public IndicoClient getIndico() throws IOException {
        /**
         * Getting path to indico_api_token.txt in current folder.
         */
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString() + "/src/test/java/com/indico";
        IndicoConfig indicoConfig = new IndicoConfig.Builder()
                .host(String.format("localhost:%d", this.port))
                .protocol("http")
                .tokenPath(s)
                .build();
        return new IndicoClient(indicoConfig);
    }

    private JSONObject getModelResponse() {
        JSONObject response = new JSONObject();
        response.put("data", new JSONObject()
                .put("modelGroups", new JSONObject()
                        .put("__typename", "ModelGroupPage")
                        .put("modelGroups", new JSONArray()
                                .put(new JSONObject()
                                        .put("__typename", "ModelGroup")
                                        .put("id", 1)
                                        .put("selectedModel", new JSONObject()
                                                .put("__typename", "Model")
                                                .put("id", 1)
                                                .put("modelInfo", new JSONObject()
                                                        .put("test_key", "test_value")
                                                        .put("load_status", "ready")
                                                        .toString()
                                                )
                                        )
                                )
                        )
                )
        );
        return response;
    }

    private JSONObject getLoadResponse() {
        JSONObject response = new JSONObject();
        response.put("data", new JSONObject()
                .put("modelLoad", new JSONObject()
                        .put("__typename", "modelLoad")
                        .put("status", "loading")
                )
        );
        return response;
    }

    private JSONObject getPredictResponse() {
        JSONObject response = new JSONObject();
        response.put("data", new JSONObject()
                .put("modelPredict", new JSONObject()
                        .put("__typename", "ModelPredict")
                        .put("jobId", "jobId_test")
                )
        );
        return response;
    }

    private JSONObject getPdfExtractionResponse() {
        JSONObject response = new JSONObject();
        response.put("data", new JSONObject()
                .put("pdfExtraction", new JSONObject()
                        .put("__typename", "PDFExtraction")
                        .put("jobId", "jobId_test")
                )
        );
        return response;
    }

    private JSONObject getJobResponse() {
        JSONObject response = new JSONObject();
        response.put("data", new JSONObject()
                .put("job", new JSONObject()
                        .put("__typename", "Job")
                        .put("result", new JSONArray()
                                .put(new JSONObject()
                                        .put("test_key", "test_value")
                                        .toString()
                                )
                        )
                        .put("status", "SUCCESS")
                        .put("ready", true)
                )
        );
        return response;
    }

    private HttpRequest getRequest(String operationName) {
        return request("/graph/api/graphql").withMethod("POST")
                .withBody(
                        json("{operationName:\"" + operationName + "\"}", MatchType.ONLY_MATCHING_FIELDS)
                )
                .withHeader("Authorization", "Bearer DemoRefreshToken");
    }
}
