package com.indico.jobs;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.indico.graphql.GraphQL;
import com.indico.JobStatusQuery;
import com.indico.JobWaitQuery;
import com.indico.JobResultQuery;
import com.indico.type.JobStatus;
import org.json.JSONArray;

public class Job {

    public final String id;
    public Boolean ready;
    private final ApolloClient apolloClient;
    private JSONArray result;

    /**
     * Class Constructor
     *
     * @param id jobId returned from graphql server
     * @param apolloClient instance of ApolloClient
     */
    public Job(String id, ApolloClient apolloClient) {
        this.id = id;
        this.apolloClient = apolloClient;
        this.result = null;
        this.ready = false;
    }

    /**
     * Waits until the Job is completed
     *
     * @param interval interval to check for job completion
     */
    public void await(int interval) {
        if (interval < 1) {
            interval = 1;
        }

        JobStatus status = JobStatus.STARTED;

        while (!this.ready) {
            ApolloCall<JobWaitQuery.Data> apolloCall = this.apolloClient.query(JobWaitQuery.builder()
                    .id(this.id)
                    .build());
            Response<JobWaitQuery.Data> response = (Response<JobWaitQuery.Data>) GraphQL.execute(apolloCall).join();
            JobWaitQuery.Data data = response.data();
            if (data.job().ready() == null) {
                this.ready = false;
            } else {
                this.ready = data.job().ready();
            }
            if (!this.ready) {
                try {
                    Thread.sleep(interval * 1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
                interval++;
            } else {
                status = data.job().status();
            }
        }

        if (!status.equals(JobStatus.SUCCESS)) {
            throw new RuntimeException("job: " + this.id + " did not end with success status");
        }
    }

    /**
     * Waits until the Job is completed
     *
     */
    public void await() {
        this.await(1);
    }

    /**
     * Returns status of the Job
     *
     * @return instance of JobStatus
     */
    public JobStatus status() {
        ApolloCall<JobStatusQuery.Data> apolloCall = this.apolloClient.query(JobStatusQuery.builder()
                .id(this.id)
                .build());
        Response<JobStatusQuery.Data> response = (Response<JobStatusQuery.Data>) GraphQL.execute(apolloCall).join();
        JobStatusQuery.Data data = response.data();
        return data.job().status();
    }

    /**
     * Synchronizes the Job and return result
     *
     * @return job result as JSONArray
     */
    public JSONArray sync() {
        this.await();
        return this.result();
    }

    /**
     * Returns the result after Job Completion
     *
     * @return result of Job, as JSONArray
     */
    public JSONArray result() {
        if (this.result != null) {
            return this.result;
        }

        ApolloCall<JobResultQuery.Data> apolloCall = this.apolloClient.query(JobResultQuery.builder()
                .id(this.id)
                .build());
        Response<JobResultQuery.Data> response = (Response<JobResultQuery.Data>) GraphQL.execute(apolloCall).join();
        JobResultQuery.Data data = response.data();
        Object jobResult = data.job().result();
        if (jobResult != null) {
            this.result = new JSONArray(jobResult.toString());
        }
        return this.result;
    }
}
