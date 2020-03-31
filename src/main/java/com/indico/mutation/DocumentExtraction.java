package com.indico.mutation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Error;
import com.apollographql.apollo.api.Response;

import com.indico.Async;
import com.indico.IndicoClient;
import com.indico.Mutation;
import com.indico.entity.DocumentExtractionOptions;
import com.indico.jobs.Job;
import com.indico.jobs.JobOptions;
import com.indico.DocumentExtractionGraphQLMutation;
import com.indico.storage.UploadFile;
import com.indico.type.FileInput;
import org.json.JSONArray;
import org.json.JSONObject;

public class DocumentExtraction implements Mutation<List<Job>> {

    private List<String> files;
    private DocumentExtractionOptions options;
    private JSONObject jsonConfig;
    private final IndicoClient indicoClient;

    public DocumentExtraction(IndicoClient indicoClient) {
        this.indicoClient = indicoClient;
        this.options = new DocumentExtractionOptions.Builder().build();
    }

    /**
     * Files to extract
     *
     * @param files File paths
     * @return DocumentExtraction
     */
    public DocumentExtraction files(List<String> files) {
        this.files = files;
        this.jsonConfig = new JSONObject();
        this.jsonConfig.put("preset_config", "simple");
        return this;
    }

    /**
     * JSON configuration for extraction
     *
     * @param jsonConfig JSON config
     * @return DocumentExtraction
     */
    public DocumentExtraction jsonConfig(JSONObject jsonConfig) {
        this.jsonConfig = jsonConfig;
        return this;
    }

    /**
     * Executes request and returns Jobs
     *
     * @return Job List
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

        DocumentExtractionGraphQLMutation x = DocumentExtractionGraphQLMutation.builder()
                .files(files)
                .jsonConfig(this.jsonConfig)
                .build();

        ApolloCall<DocumentExtractionGraphQLMutation.Data> apolloCall = indicoClient.apolloClient.mutate(DocumentExtractionGraphQLMutation.builder()
                .files(files)
                .jsonConfig(this.jsonConfig)
                .build());

        Response<DocumentExtractionGraphQLMutation.Data> response = (Response<DocumentExtractionGraphQLMutation.Data>) Async.executeSync(apolloCall).join();
        if (response.hasErrors()) {
            StringBuilder errors = new StringBuilder();
            for (Error err : response.errors()) {
                errors.append(err.toString() + "\n");
            }
            String msg = errors.toString();
            throw new RuntimeException("Failed to extract documents due to following error: \n" + msg);
        }
        List<String> jobIds = response.data().documentExtraction().jobIds();
        List<Job> jobs = new ArrayList<>();
        for (String id : jobIds) {
            Job job = new Job(this.indicoClient.apolloClient, id);
            jobs.add(job);
        }

        return jobs;
    }

    private JSONArray upload(List<String> filePath) throws IOException {
        UploadFile uploadRequest = new UploadFile(this.indicoClient);
        return uploadRequest.filePaths(this.files).call();
    }
}
