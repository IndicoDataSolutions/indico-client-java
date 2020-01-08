package com.indico;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.apollographql.apollo.ApolloClient;
import com.indico.api.ModelGroup;
import com.indico.api.PdfExtraction;
import com.indico.api.PdfReader;
import com.indico.auth.TokenAuthenticator;
import com.indico.config.IndicoConfig;
import com.indico.jobs.Job;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

public class IndicoClient implements AutoCloseable {

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
    public IndicoClient(IndicoConfig indicoConfig) throws IOException, FileNotFoundException {
        String serverURL = indicoConfig.protocol + "://" + indicoConfig.host;

        this.okHttpClient = new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator(serverURL, indicoConfig.apiToken))
                .build();

        this.dispatcher = dispatcher(indicoConfig.maxConnections);

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
    public IndicoClient() throws IOException, FileNotFoundException {
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
     * @param pdfReader instance of PdfReader
     * @return instance of Job
     * @throws IOException
     * @throws FileNotFoundException
     */
    public Job pdfExtraction(PdfReader pdfReader) throws IOException, FileNotFoundException {
        return PdfExtraction.extract(this.apolloClient, pdfReader);
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
    private ThreadPoolExecutor dispatcher(int maxConnections) {
        return new ThreadPoolExecutor(0, maxConnections, 60, TimeUnit.SECONDS,
                new SynchronousQueue<>(), (Runnable runnable)
                -> new Thread(runnable, "Apollo Dispatcher"));
    }
}
