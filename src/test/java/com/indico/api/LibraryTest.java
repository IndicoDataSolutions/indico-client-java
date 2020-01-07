package com.indico.api;

import com.indico.IndicoClient;
import com.indico.jobs.Job;
import com.indico.Setup;
import com.indico.type.JobStatus;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

public class LibraryTest {

    private static ClientAndServer mockServer;
    private static IndicoClient indico;
    private static ModelGroup modelGroup;

    @BeforeAll
    public static void setup() throws IOException {
        Setup setup = new Setup(8000);
        mockServer = setup.getMockServer();
        /**
         * getIndico throws IOException if indico_api_token.txt is removed from
         * test com.indico, make sure that it contains DemoApiToken, Port 8000
         * should be open for test to create mock server.
         */
        indico = setup.getIndico();
        modelGroup = indico.ModelGroup(1);
    }

    @AfterAll
    public static void stopMockServer() throws Exception {
        mockServer.stop();
        indico.close();
    }

    @Test
    void testInfo() {
        JSONObject info = modelGroup.info();
        Assert.assertEquals("test_value", info.getString("test_key"));
        Assert.assertEquals("ready", info.getString("load_status"));
    }

    @Test
    void testLoad() {
        Assert.assertEquals("ready", modelGroup.load());
        Assert.assertEquals("ready", modelGroup.load(1));
    }

    @Test
    void testSelectedModel() {
        JSONObject model = modelGroup.getSelectedModel();
        Assert.assertEquals(1, model.getInt("id"));
        Assert.assertEquals("test_value", model.getJSONObject("modelInfo").getString("test_key"));
        Assert.assertEquals("ready", model.getJSONObject("modelInfo").getString("load_status"));
    }

    @Test
    void testPredict() {
        JSONArray json = modelGroup.predict(new ArrayList<String>()).sync();
        JSONObject result = json.getJSONObject(0);
        Assert.assertEquals("test_value", result.getString("test_key"));
    }

    @Test
    void testPdfExtraction() throws IOException {
        JSONArray json = indico.pdfExtraction(new ArrayList<String>()).sync();
        JSONObject result = json.getJSONObject(0);
        Assert.assertEquals("test_value", result.getString("test_key"));
    }

    @Test
    void testJobStatus() {
        Job job = modelGroup.predict(new ArrayList<String>());
        Assert.assertEquals(JobStatus.SUCCESS, job.status());
    }
}
