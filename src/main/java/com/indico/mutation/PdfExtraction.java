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
import com.indico.entity.PdfExtractionOptions;
import com.indico.jobs.Job;
import com.indico.jobs.JobOptions;
import com.indico.PdfExtractionGraphQLMutation;

public class PdfExtraction implements Mutation<Job> {

    private List<String> data;
    private PdfExtractionOptions options;
    private JobOptions jobOptions;
    private final ApolloClient apolloClient;

    public PdfExtraction(ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
        this.options = new PdfExtractionOptions.Builder().build();
    }

    /**
     * List of PDF File(s)
     * 
     * @param data List of files
     * @return PdfExtraction
     */
    public PdfExtraction data(List<String> data) {
        this.data = data;
        return this;
    }

    /**
     * Options for pdf extraction
     * 
     * @param options Extraction options
     * @return PdfExtraction
     */
    public PdfExtraction pdfExtractionOptions(PdfExtractionOptions options) {
        this.options = options;
        return this;
    }

    /**
     * Job Options for Job
     * 
     * @param jobOptions Job options
     * @return PdfExtraction
     */
    public PdfExtraction jobOptions(JobOptions jobOptions) {
        this.jobOptions = jobOptions;
        return this;
    }

    /**
     * Executes request and returns job
     * 
     * @return Job 
     */
    @Override
    public Job execute() {
        List<String> files;
        try {
            files = this.process(this.data);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e.fillInStackTrace());
        }

        ApolloCall<PdfExtractionGraphQLMutation.Data> apolloCall = apolloClient.mutate(PdfExtractionGraphQLMutation.builder()
                .data(files)
                .singleColumn(this.options.singleColumn)
                .text(this.options.text)
                .rawText(this.options.rawText)
                .tables(this.options.tables)
                .metadata(this.options.metadata)
                .build());
        Response<PdfExtractionGraphQLMutation.Data> response = (Response<PdfExtractionGraphQLMutation.Data>) Async.executeSync(apolloCall).join();
        String jobId = response.data().pdfExtraction().jobId();
        return new Job(this.apolloClient, jobId);
    }

    private List<String> process(List<String> files) throws IOException {
        ArrayList<String> pdfList = new ArrayList<>();
        for (String url : files) {
            File file = new File(url);
            if (file.exists()) {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                String encodedb64 = Base64.getEncoder().encodeToString(fileBytes);
                pdfList.add(encodedb64);
            } else {
                pdfList.add(url);
            }
        }
        return pdfList;
    }
}
