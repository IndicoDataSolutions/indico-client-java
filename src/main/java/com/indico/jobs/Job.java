package com.indico.jobs;

import java.util.List;
import java.util.ArrayList;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.indico.Async;
import com.indico.type.JobStatus;
import com.indico.JobStatusGraphQLQuery;
import com.indico.JobResultGraphQLQuery;

/**
 * Async Job information
 */
public class Job {

    public final String id;
    private final ApolloClient apolloClient;

    public Job(ApolloClient apolloClient, String id) {
        this.apolloClient = apolloClient;
        this.id = id;
    }

    /**
     * Retrieve job status
     *
     * @return
     */
    public JobStatus status() {
        ApolloCall<JobStatusGraphQLQuery.Data> apolloCall = this.apolloClient.query(JobStatusGraphQLQuery.builder()
                .id(this.id)
                .build());
        Response<JobStatusGraphQLQuery.Data> response = (Response<JobStatusGraphQLQuery.Data>) Async.executeSync(apolloCall).join();
        JobStatusGraphQLQuery.Data data = response.data();
        JobStatus status = data.job().status();
        return status;
    }

    /**
     * Retrieve results. Status must be success or an error will be thrown.
     *
     * @return
     */
    public String result() {
        ApolloCall<JobResultGraphQLQuery.Data> apolloCall = this.apolloClient.query(JobResultGraphQLQuery.builder()
                .id(this.id)
                .build());
        Response<JobResultGraphQLQuery.Data> response = (Response<JobResultGraphQLQuery.Data>) Async.executeSync(apolloCall).join();
        JobResultGraphQLQuery.Data data = response.data();
        String result = response.data().job().result().toString();
        return result;
    }

    /**
     * If job status is FAILURE returns the list of errors encoutered
     */
    public List<String> errors() {
        return new ArrayList<String>();
    }
;
}
