package com.indico.api;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Base64;
import java.nio.file.Files;
import com.indico.PdfExtractionMutation;
import com.indico.graphql.GraphQL;
import com.indico.jobs.Job;

public class PdfExtraction {

    /**
     *
     * @param apolloClient instance of ApolloClient
     * @param data list of pdf files
     * @param pdfExtractionOptions extra options for output
     * @see PdfExtractionOptions
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static Job extract(ApolloClient apolloClient, List<String> data, PdfExtractionOptions pdfExtractionOptions) throws FileNotFoundException, IOException {
        ArrayList<String> pdfList = new ArrayList<String>();
        for (String url : data) {
            pdfList.add(PdfExtraction.preProcess(url));
        }
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

    /**
     * Returns base64 encoded string of file or assumes the url passed is
     * already a base64 string
     *
     * @param url location of pdf file
     * @return url or base64 encoded content of file
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static String preProcess(String url) throws FileNotFoundException, IOException {
        File file = new File(url);
        if (file.exists()) {
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String encodedb64 = Base64.getEncoder().encodeToString(fileBytes);
            return encodedb64;
        } else {
            return url;
        }
    }
}
