package com.indico.api;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import com.indico.PdfExtractionMutation;
import com.indico.graphql.GraphQL;
import com.indico.jobs.Job;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PdfExtraction {

    private final List<String> files;
    private final PdfExtractionOptions pdfExtractionOptions;
    private final ApolloClient apolloClient;

    private PdfExtraction(Builder builder) throws FileNotFoundException, IOException {
        this.files = this.process(builder.files);
        this.pdfExtractionOptions = builder.pdfExtractionOptions;
        this.apolloClient = builder.apolloClient;
    }

    private List<String> process(List<String> files) throws FileNotFoundException, IOException {
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

    /**
     *
     * @return Instance of Job
     * @throws FileNotFoundException
     * @throws IOException
     */
    public Job extract() throws FileNotFoundException, IOException {
        ApolloCall<PdfExtractionMutation.Data> apolloCall = apolloClient.mutate(PdfExtractionMutation.builder()
                .data(this.files)
                .singleColumn(this.pdfExtractionOptions.singleColumn)
                .text(this.pdfExtractionOptions.text)
                .rawText(this.pdfExtractionOptions.rawText)
                .tables(this.pdfExtractionOptions.tables)
                .metadata(this.pdfExtractionOptions.metadata)
                .build());
        Response<PdfExtractionMutation.Data> response = (Response<PdfExtractionMutation.Data>) GraphQL.execute(apolloCall).join();
        String jobId = response.data().pdfExtraction().jobId();
        return new Job(jobId, this.apolloClient);
    }

    public static class Builder {

        protected List<String> files = null;
        protected PdfExtractionOptions pdfExtractionOptions = null;
        protected final ApolloClient apolloClient;

        public Builder(ApolloClient apolloClient) {
            this.apolloClient = apolloClient;
        }

        /**
         * 
         * @param files Pdf File List
         * @return 
         */
        public Builder setFiles(List<File> files) {
            ArrayList<String> filePaths = new ArrayList<>();
            files.forEach((file) -> {
                filePaths.add(file.getPath());
            });
            this.files = filePaths;
            return this;
        }

        /**
         * 
         * @param filePaths Pdf Files or URL(s) as String List
         * @return 
         */
        public Builder setFilePaths(List<String> filePaths) {
            this.files = filePaths;
            return this;
        }

        /**
         * 
         * @param stream Stream containing Pdf Files or URL(s) as String
         * @return 
         */
        public Builder setStream(Stream<String> stream) {
            this.files = stream.collect(Collectors.toList());
            return this;
        }

        /**
         * 
         * @param pdfExtractionOptions sets PdfExtractionOptions for extraction
         * @return 
         */
        public Builder setPdfExtractionOptions(PdfExtractionOptions pdfExtractionOptions) {
            this.pdfExtractionOptions = pdfExtractionOptions;
            return this;
        }

        /**
         * 
         * @return Instance of PdfExtraction
         * @throws FileNotFoundException
         * @throws IOException 
         */
        public PdfExtraction build() throws FileNotFoundException, IOException {
            if (this.files == null) {
                throw new RuntimeException("Pdf File List Cannot Be Empty");
            }
            if (this.pdfExtractionOptions == null) {
                this.pdfExtractionOptions = new PdfExtractionOptions.Builder().build();
            }
            return new PdfExtraction(this);
        }
    }
}
