package com.indico;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import com.apollographql.apollo.ApolloClient;
import okhttp3.OkHttpClient;

import com.indico.jobs.JobQuery;
import com.indico.storage.RetrieveBlob;
import com.indico.storage.PurgeBlob;
import com.indico.query.WorkflowQuery;
import com.indico.mutation.WorkflowSubmission;
import com.indico.auth.TokenAuthenticator;
import com.indico.query.ModelGroupQuery;
import com.indico.mutation.ModelGroupLoad;
import com.indico.mutation.ModelGroupPredict;
import com.indico.mutation.PdfExtraction;

/**
 * Indico client with all available top level query and mutations
 */
public class IndicoClient implements AutoCloseable {

    public final IndicoConfig config;
    private final OkHttpClient okHttpClient;
    private final ApolloClient apolloClient;
    private final ThreadPoolExecutor dispatcher;

    public IndicoClient(IndicoConfig config) {
        this.config = config;
        String serverURL = config.protocol + "://" + config.host;

        this.okHttpClient = new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator(serverURL, config.apiToken))
                .build();

        this.dispatcher = dispatcher(config.maxConnections);

        this.apolloClient = ApolloClient.builder()
                .serverUrl(serverURL + "/graph/api/graphql")
                .okHttpClient(this.okHttpClient)
                .dispatcher(this.dispatcher)
                .build();
    }

    /**
     * Create a new Query for ModelGroup
     * @return ModelGroupQuery
     */
    public ModelGroupQuery modelGroupQuery() {
        return new ModelGroupQuery(this.apolloClient);
    }

    /**
     * Create a new mutation to predict data
     * @return ModelGroupPredict
     */
    public ModelGroupPredict modelGroupPredict() {
        return new ModelGroupPredict(this.apolloClient);
    }

    /**
     * Create a new mutation to load model in ModelGroup
     * @return ModelGroupLoad
     */
    public ModelGroupLoad modelGroupLoad() {
        return new ModelGroupLoad(this.apolloClient);
    }

    /**
     * Create a new mutation to submit PDF(s) to process by a PdfExtraction
     * @return PdfExtraction
     */
    public PdfExtraction pdfExtraction() {
        return new PdfExtraction(this.apolloClient);
    }

    /**
     * Create a new query for a workflow
     *
     * @return WorkflowQuery
     */
    public WorkflowQuery workflowQuery() {
        return new WorkflowQuery();
    }

    /**
     * Create a new mutation to submit documents to process by a workflow
     *
     * @return WorkflowSubmission
     */
    public WorkflowSubmission workflowSubmission() {
        return new WorkflowSubmission();
    }

    /**
     * Create a query to retrieve async job info
     *
     * @return JobQuery
     */
    public JobQuery jobQuery() {
        return new JobQuery(this.apolloClient);
    }

    /**
     * Retrieve a blob from indico blob storage
     *
     * @return RetrieveBlob
     */
    public RetrieveBlob retrieveBlob() {
        return new RetrieveBlob();
    }

    /**
     * Create a request to delete a blob fron indico blob storage
     *
     * @return PurgeBlob
     */
    public PurgeBlob purgeBlob() {
        return new PurgeBlob();
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
