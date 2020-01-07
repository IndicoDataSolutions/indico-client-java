package com.indico;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.apollographql.apollo.ApolloClient;
import com.indico.api.ModelGroup;
import com.indico.api.PdfExtraction;
import com.indico.api.PdfExtractionOptions;
import com.indico.auth.Authentication;
import com.indico.auth.TokenAuthenticator;
import com.indico.config.IndicoConfig;
import com.indico.jobs.Job;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class Indico implements AutoCloseable {

    private final OkHttpClient okHttpClient;
    private final ApolloClient apolloClient;
    private final ThreadPoolExecutor dispatcher;

    /**
     * Class Constructor
     *
     * @param indicoConfig instance of IndicoConfig
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Indico(IndicoConfig indicoConfig) throws IOException, FileNotFoundException {
        String serverURL = indicoConfig.protocol + "://" + indicoConfig.host;
        String apiToken = Authentication.resolveApiToken(indicoConfig.tokenPath);

        this.okHttpClient = new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator(serverURL, apiToken))
                .build();

        this.dispatcher = dispatcher();

        this.apolloClient = ApolloClient.builder()
                .serverUrl(serverURL + "/graph/api/graphql")
                .okHttpClient(this.okHttpClient)
                .dispatcher(this.dispatcher)
                .build();
    }

    /**
     * Class Constructor with default IndicoConfig
     *
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Indico() throws IOException, FileNotFoundException {
        this(new IndicoConfig.Builder().build());
    }

    /**
     * Returns an instance of ModelGroup with specified model group id
     *
     * @param id model group id
     * @return instance of ModelGroup
     */
    public ModelGroup ModelGroup(int id) {
        return new ModelGroup(this.apolloClient, id);
    }

    /**
     * Returns Job for extraction of data from pdf files with specified options
     *
     * @param data list of files paths
     * @param pdfExtractionOptions instance of PdfExtractionOptions
     * @return instance of Job
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Job pdfExtraction(List<String> data, PdfExtractionOptions pdfExtractionOptions) throws IOException, FileNotFoundException {
        return PdfExtraction.extract(this.apolloClient, data, pdfExtractionOptions);
    }

    /**
     * Returns Job for extraction of data from pdf files with default options
     *
     * @param data list of files paths
     * @return instance of Job
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Job pdfExtraction(List<String> data) throws IOException, FileNotFoundException {
        PdfExtractionOptions pdfExtractionOptions = new PdfExtractionOptions.Builder().build();
        return pdfExtraction(data, pdfExtractionOptions);
    }

    /**
     * Closes the connnection to graphql server since the ThreadPool remains
     * active for several seconds before closing due to multiple asynchronous
     * queries and prevents JVM from closing for 60 seconds.
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        this.dispatcher.shutdown();
        this.okHttpClient.dispatcher().executorService().shutdown();
    }

    /**
     * Synchronous Queue dispatcher for ApolloClient
     *
     * @return instance of ThreadPoolExecutor
     */
    private ThreadPoolExecutor dispatcher() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), (Runnable runnable)
                -> new Thread(runnable, "Apollo Dispatcher"));
    }
}
