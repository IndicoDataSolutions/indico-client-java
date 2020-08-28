package com.indico.mutation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.indico.Async;
import com.indico.IndicoClient;
import com.indico.Mutation;
import com.indico.WorkflowJobGraphQLMutation;
import com.indico.jobs.Job;
import com.indico.storage.UploadFile;
import com.indico.type.FileInput;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkflowJob implements Mutation<List<Job>> {

    private final IndicoClient client;
    private List<String> files;
    private int id;

    public WorkflowJob(IndicoClient client) {
        this.client = client;
    }

    /**
     * List of local file paths to submit
     * @param files File paths
     * @return WorkflowJob
     */
    public WorkflowJob files(List<String> files) {
        this.files = files;
        return this;
    }

    /**
     * Id of workflow to submit files to
     * @param id Workflow Id
     * @return WorkflowSubmission
     */
    public WorkflowJob workflowId(int id) {
        this.id = id;
        return this;
    }

    /**
     * Executes request and returns Job
     * @return Job
     */
    @Override
    public List<Job> execute() {
        JSONArray fileMetadata;
        List<FileInput> files = new ArrayList<>();
        try {
            fileMetadata = this.upload(this.files);
            for (Object f : fileMetadata) {
                JSONObject uploadMeta = (JSONObject) f;
                JSONObject meta = new JSONObject();
                meta.put("name", uploadMeta.getString("name"));
                meta.put("path", uploadMeta.getString("path"));
                meta.put("upload_type", uploadMeta.getString("upload_type"));
                FileInput input = FileInput.builder().filename(((JSONObject) f).getString("name")).filemeta(meta).build();
                files.add(input);
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e.fillInStackTrace());
        }

        ApolloCall<WorkflowJobGraphQLMutation.Data> apolloCall = this.client.apolloClient.mutate(WorkflowJobGraphQLMutation.builder()
                .files(files)
                .workflowId(this.id)
                .build());

        Response<WorkflowJobGraphQLMutation.Data> response = (Response<WorkflowJobGraphQLMutation.Data>) Async.executeSync(apolloCall).join();
        if (response.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (Error err : response.errors()) {
                errors.append(err.toString() + "\n");
            }
            String msg = errors.toString();
            throw new RuntimeException("Failed to extract documents due to following error: \n" + msg);
        }
        WorkflowJobGraphQLMutation.WorkflowSubmission workflowSubmission = response.data().workflowSubmission();
        List<String> jobIds = workflowSubmission.jobIds();
        if(jobIds.isEmpty()) {
            throw new RuntimeException("Failed to submit to workflow " + this.id);
        }
        List<Job> jobs = new ArrayList<>();
        jobIds.forEach(jobId -> jobs.add(new Job(this.client.apolloClient, jobId)));
        return jobs;
    }

    private JSONArray upload(List<String> filePaths) throws IOException {
        UploadFile uploadRequest = new UploadFile(this.client);
        return uploadRequest.filePaths(filePaths).call();
    }
}
