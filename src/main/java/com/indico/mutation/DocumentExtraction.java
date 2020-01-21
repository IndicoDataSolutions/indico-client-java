package com.indico.mutation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;

import com.indico.Async;
import com.indico.Mutation;
import com.indico.entity.DocumentExtractionOptions;
import com.indico.jobs.Job;
import com.indico.jobs.JobOptions;
import com.indico.DocumentExtractionGraphQLMutation;

public class DocumentExtraction implements Mutation<Job> {

    private List<String> data;
    private DocumentExtractionOptions options;
    private JobOptions jobOptions;
    private final ApolloClient apolloClient;

    public DocumentExtraction(ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
        this.options = new DocumentExtractionOptions.Builder().build();
    }

    public DocumentExtraction data(List<String> data) {
        this.data = data;
        return this;
    }

    public DocumentExtraction DocumentExtractionOptions(DocumentExtractionOptions options) {
        this.options = options;
        return this;
    }

    public DocumentExtraction jobOptions(JobOptions jobOptions) {
        this.jobOptions = jobOptions;
        return this;
    }

    @Override
    public Job execute() {
        List<String> files;
        try {
            files = this.process(this.data);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e.fillInStackTrace());
        }

        ApolloCall<DocumentExtractionGraphQLMutation.Data> apolloCall = apolloClient.mutate(DocumentExtractionGraphQLMutation.builder()
                .data(files)
                .singleColumn(this.options.singleColumn)
                .text(this.options.text)
                .rawText(this.options.rawText)
                .tables(this.options.tables)
                .metadata(this.options.metadata)
                .forceRender(this.options.forceRender)
                .detailed(this.options.detailed)
                .build());
        Response<DocumentExtractionGraphQLMutation.Data> response = (Response<DocumentExtractionGraphQLMutation.Data>) Async.executeSync(apolloCall).join();
        String jobId = response.data().documentExtraction().jobId();
        return new Job(this.apolloClient, jobId);
    }

    private List<String> process(List<String> files) throws IOException {
        ArrayList<String> docList = new ArrayList<>();
        for (String url : files) {
            File file = new File(url);
            if (file.exists()) {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                String encodedb64 = Base64.getEncoder().encodeToString(fileBytes);
                docList.add(encodedb64);
            } else {
                docList.add(url);
            }
        }
        return docList;
    }
}
