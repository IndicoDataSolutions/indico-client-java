package com.indico.mutation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;
import com.indico.Async;
import com.indico.IndicoClient;
import com.indico.Mutation;
import com.indico.WorkflowSubmissionGraphQLMutation;
import com.indico.storage.UploadFile;
import com.indico.storage.UploadStream;
import com.indico.type.FileInput;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;


public class WorkflowSubmission implements Mutation<List<Integer>> {

    private final IndicoClient client;
    private List<String> files;
    private int id;
    private UUID duplicationId;
    private byte[] stream;
    private Map<String, byte[]> streams;
    private final Logger logger = LogManager.getLogger(WorkflowSubmission.class);

    public WorkflowSubmission(IndicoClient client) {
        this.client = client;
    }
    
    /**
     * List of local file paths to submit
     * @param files File paths
     * @return WorkflowSubmission
     */
    public WorkflowSubmission files(List<String> files) {
        this.files = files;
        return this;
    }

    /**
     *
     * @param streams Dictionary of String identifiers and byte streams to upload.
     * @return
     */
    public WorkflowSubmission byteStreams(Map<String, byte[]> streams){
        this.streams = streams;
        return this;
    }

    /**
     * Id of workflow to submit files to
     * @param id Workflow Id
     * @return WorkflowSubmission
     */
    public WorkflowSubmission workflowId(int id) {
        this.id = id;
        return this;
    }

    /**
     * A UUID representing a unique set of files and workflow activity.
     * This optional parameter helps the platform detect and prevent duplicate submissions.
     * @param id
     * @return
     */
    public WorkflowSubmission duplicationId(UUID id){
        this.duplicationId = id;
        return this;
    }

    /**
     * Executes request and returns Submissions
     * @return Integer List
     */
    @Override
    public List<Integer> execute() {
        JSONArray fileMetadata;
        List<FileInput> files = new ArrayList<FileInput>();
        try {
            if(this.files != null)
            {
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
            }
            if(this.streams != null){
                fileMetadata = this.uploadBytes(this.streams);
                for (Object f : fileMetadata) {
                    JSONObject uploadMeta = (JSONObject) f;
                    JSONObject meta = new JSONObject();
                    meta.put("name", uploadMeta.getString("name"));
                    meta.put("path", uploadMeta.getString("path"));
                    meta.put("upload_type", uploadMeta.getString("upload_type"));
                    FileInput input = FileInput.builder().filename(((JSONObject) f).getString("name")).filemeta(meta).build();
                    files.add(input);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e.fillInStackTrace());
        }
        if(this.duplicationId == null){
            this.duplicationId = UUID.randomUUID();
        }
        try{
        ApolloCall<WorkflowSubmissionGraphQLMutation.Data> apolloCall = this.client.apolloClient.mutate(WorkflowSubmissionGraphQLMutation.builder()
                .files(files)
                .workflowId(this.id)
                .duplicationId(this.duplicationId.toString())
                .build());

        Response<WorkflowSubmissionGraphQLMutation.Data> response = (Response<WorkflowSubmissionGraphQLMutation.Data>) Async.executeSync(apolloCall).get();
        if (response.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (Error err : response.errors()) {
                errors.append(err.toString() + "\n");
            }

            String msg = errors.toString();
            logger.error("Errors encountered during submission: " + msg);
            throw new RuntimeException("Failed to submit due to following error: \n" + msg);
        }
        WorkflowSubmissionGraphQLMutation.WorkflowSubmission workflowSubmission = response.data().workflowSubmission();
        if(workflowSubmission.isDuplicateRequest()){
            logger.debug("Duplicate submission sent for submission ids " + workflowSubmission.submissionIds().toString());
        }
        return workflowSubmission.submissionIds();
        }catch (CompletionException | ExecutionException | InterruptedException ex){
            throw new RuntimeException("Call to submit submission failed", ex);
        }
    }

    private JSONArray upload(List<String> filePaths) throws IOException {
        UploadFile uploadRequest = new UploadFile(this.client);
        return uploadRequest.filePaths(filePaths).call();
    }

    private JSONArray uploadBytes(Map<String, byte[]> stream) throws IOException {
        UploadStream uploadRequest = new UploadStream(this.client);
        return uploadRequest.byteStream(stream).call();
    }
}
