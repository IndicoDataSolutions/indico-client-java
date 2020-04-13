package com.indico;

import com.indico.entity.ModelGroup;
import com.indico.jobs.Job;
import com.indico.mutation.ModelGroupLoad;
import com.indico.type.JobStatus;
import com.indico.type.ModelStatus;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;

public class LibraryTest {

    private static ClientAndServer mockServer;
    private static IndicoClient indico;
    private static ModelGroup mg;

    @BeforeAll
    public static void setup() throws IOException {
        Setup setup = new Setup(8000);
        mockServer = setup.getMockServer();
        indico = setup.getIndico();
        mg = indico.modelGroupQuery().id(1).query();
    }

    @AfterAll
    public static void stopMockServer() throws Exception {
        mockServer.stop();
        indico.close();
    }

    @Test
    void testModelGroup() {
        Assert.assertEquals(1, mg.id);
        Assert.assertEquals("testModelGroup", mg.name);
        Assert.assertEquals(ModelStatus.COMPLETE, mg.status);
        Assert.assertEquals(1, mg.selectedModel.id);
        Assert.assertEquals(ModelStatus.COMPLETE, mg.selectedModel.status);
    }

    @Test
    void testLoad() {
        ModelGroupLoad mgl = indico.modelGroupLoad();
        Assert.assertEquals("loading", mgl.modelGroup(mg).execute());
    }

    @Test
    void testPredict() {
        Job job = indico.modelGroupPredict()
                .modelGroup(mg)
                .data(new ArrayList<String>())
                .execute();
        Assert.assertEquals(JobStatus.SUCCESS, job.status());
        JSONArray result = job.results();
        Assert.assertEquals("test_value", result.getJSONObject(0).getString("test_key"));
    }
}
