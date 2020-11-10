package com.indico;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.apollographql.apollo.ApolloClient;
import com.indico.mutation.*;
import com.indico.query.*;
import com.indico.storage.UploadFile;
import okhttp3.OkHttpClient;

import com.indico.jobs.JobQuery;
import com.indico.storage.RetrieveBlob;
import com.indico.storage.PurgeBlob;
import com.indico.mutation.WorkflowSubmission;
import com.indico.request.GraphQLRequest;

/**
 * Indico client with all available top level query and mutations
 */
public class IndicoClient implements AutoCloseable {

    public final IndicoConfig config;
    public final OkHttpClient okHttpClient;
    public final ApolloClient apolloClient;
    private final ThreadPoolExecutor dispatcher;

    public IndicoClient(IndicoConfig config) {
        this.config = config;
        String serverURL = config.protocol + "://" + config.host;

        this.okHttpClient = new OkHttpClient.Builder()
                .authenticator(new TokenAuthenticator(serverURL, config.apiToken))
                .readTimeout(config.connectionReadTimeout, TimeUnit.SECONDS)
                .writeTimeout(config.connectionWriteTimeout, TimeUnit.SECONDS)
                .build();

        this.dispatcher = dispatcher(config.maxConnections);

        this.apolloClient = ApolloClient.builder()
                .serverUrl(serverURL + "/graph/api/graphql")
                .okHttpClient(this.okHttpClient)
                .dispatcher(this.dispatcher)
                .build();
    }

    /**
     * Create a new GraphQL Request
     *
     * @return GraphQLRequest
     */
    public GraphQLRequest graphQLRequest() {
        return new GraphQLRequest(this);
    }

    /**
     * Create a new Query for ModelGroup
     *
     * @return ModelGroupQuery
     */
    public ModelGroupQuery modelGroupQuery() {
        return new ModelGroupQuery(this.apolloClient);
    }

    /**
     * Create a new Query for TrainingModelWithProgress
     *
     * @return TrainingModelWithProgressQuery
     */
    public TrainingModelWithProgressQuery trainingModelWithProgressQuery() {
        return new TrainingModelWithProgressQuery(this);
    }

    /**
     * Create a new mutation to predict data
     *
     * @return ModelGroupPredict
     */
    public ModelGroupPredict modelGroupPredict() {
        return new ModelGroupPredict(this.apolloClient);
    }

    /**
     * Create a new mutation to load model in ModelGroup
     *
     * @return ModelGroupLoad
     */
    public ModelGroupLoad modelGroupLoad() {
        return new ModelGroupLoad(this.apolloClient);
    }

    /**
     * Create a new mutation to submit document for extraction
     *
     * @return DocumentExtraction
     */
    public DocumentExtraction documentExtraction() {
        return new DocumentExtraction(this);
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
        return new WorkflowSubmission(this);
    }

    public WorkflowSubmissionDetailed workflowSubmissionDetailed() {
        return new WorkflowSubmissionDetailed(this);
    }

    public GetSubmission getSubmission() {
        return new GetSubmission(this);
    }

    public UpdateSubmission updateSubmission() {
        return new UpdateSubmission(this);
    }

    public ListSubmissions listSubmissions() {
        return new ListSubmissions(this);
    }

    public GenerateSubmissionResult generateSubmissionResult() {
        return new GenerateSubmissionResult(this);
    }

    public SubmissionResult submissionResult() {
        return new SubmissionResult(this);
    }

    /**
     * Create a new query to list workflows for dataset
     *
     * @return ListWorkflowsForDatasetQuery
     */
    public ListWorkflows listWorkflows() {
        return new ListWorkflows(this);
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
        return new RetrieveBlob(this);
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
     * Uploads files
     *
     * @return UploadFile
     */
    public UploadFile uploadFile() {
        return new UploadFile(this);
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
                new LinkedBlockingQueue<>(), (Runnable runnable)
                -> new Thread(runnable, "Apollo Dispatcher"));
    }
}
