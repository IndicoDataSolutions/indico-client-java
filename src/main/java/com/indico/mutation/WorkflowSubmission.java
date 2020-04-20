package com.indico.mutation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.api.Error;
import com.indico.Async;
import com.indico.IndicoClient;
import com.indico.Mutation;
import com.indico.WorkflowSubmissionGraphQLMutation;
import com.indico.jobs.Job;
import com.indico.storage.UploadFile;
import com.indico.type.FileInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class WorkflowSubmission implements Mutation<Job> {

    private final IndicoClient client;
    private List<String> files;
    private int id;

    public WorkflowSubmission(IndicoClient client) {
        this.client = client;
    }

    public WorkflowSubmission files(List<String> files) {
        this.files = files;
        return this;
    }

    public WorkflowSubmission workflowId(int id) {
        this.id = id;
        return this;
    }

    public Job execute() {
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

        ApolloCall<WorkflowSubmissionGraphQLMutation.Data> apolloCall = this.client.apolloClient.mutate(WorkflowSubmissionGraphQLMutation.builder()
                .files(files)
                .workflowId(this.id)
                .build());

        Response<WorkflowSubmissionGraphQLMutation.Data> response = (Response<WorkflowSubmissionGraphQLMutation.Data>) Async.executeSync(apolloCall).join();
        if (response.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (Error err : response.errors()) {
                errors.append(err.toString() + "\n");
            }
            String msg = errors.toString();
            throw new RuntimeException("Failed to extract documents due to following error: \n" + msg);
        }
        String jobId = response.data().workflowSubmission().jobId();
        if (jobId == null) {
            throw new RuntimeException("Failed to submit to workflow " + this.id);
        }

        return new Job(this.client.apolloClient, jobId);
    }

    private JSONArray upload(List<String> filePaths) throws IOException {
        UploadFile uploadRequest = new UploadFile(this.client);
        return uploadRequest.filePaths(filePaths).call();
    }
}
