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

public class PdfExtraction {

    /**
     *
     * @param apolloClient instance of ApolloClient
     * @param pdfReader instance of PdfReader
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Job extract(ApolloClient apolloClient, PdfReader pdfReader) throws FileNotFoundException, IOException {
        List<String> pdfList = pdfReader.getList();
        PdfExtractionOptions pdfExtractionOptions = pdfReader.getPdfExtractionOptions();
        ApolloCall<PdfExtractionMutation.Data> apolloCall = apolloClient.mutate(PdfExtractionMutation.builder()
                .data(pdfList)
                .singleColumn(pdfExtractionOptions.singleColumn)
                .text(pdfExtractionOptions.text)
                .rawText(pdfExtractionOptions.rawText)
                .tables(pdfExtractionOptions.tables)
                .metadata(pdfExtractionOptions.metadata)
                .build());
        Response<PdfExtractionMutation.Data> response = (Response<PdfExtractionMutation.Data>) GraphQL.execute(apolloCall).join();
        String jobId = response.data().pdfExtraction().jobId();
        return new Job(jobId, apolloClient);
    }
}
