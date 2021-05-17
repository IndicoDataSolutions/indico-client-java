package com.indico.jobs;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;
import com.indico.*;
import com.indico.type.JobStatus;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Async Job information
 */
public class Job {

    public final String id;
    private final IndicoClient indicoClient;

    public Job(IndicoClient indicoClient, String id) {
        this.indicoClient = indicoClient;
        this.id = id;
    }

    /**
     * Retrieve job status
     *
     * @return JobStatus
     */
    public JobStatus status() {
        try{
        ApolloCall<JobStatusGraphQLQuery.Data> apolloCall = this.indicoClient.apolloClient.query(JobStatusGraphQLQuery.builder()
                .id(this.id)
                .build());
        Response<JobStatusGraphQLQuery.Data> response = Async.executeSync(apolloCall).get();
        JobStatusGraphQLQuery.Data data = response.data();
        JobStatus status = data.job().status();
        return status;
        }catch (CompletionException | ExecutionException | InterruptedException ex){
            throw new RuntimeException("Call for the job status failed", ex);
        }
    }

    public String resultAsString() {
        String result = this.fetchResult();
        return result;
    }

    /**
     * Retrieve result. Status must be success or an error will be thrown.
     *
     * @return JSONObject
     */
    public JSONObject result() {
        String result = this.fetchResult();
        return new JSON(result).asJSONObject();
    }

    /**
     * Retrieve results. Status must be success or an error will be thrown.
     *
     * @return JSONArray
     */
    public JSONArray results() {
        String result = this.fetchResult();
        return new JSON(result).asJSONArray();
    }

    /**
     * Retrieve results as String
     *
     * @return Result String
     */
    private String fetchResult() {
        try{
        ApolloCall<JobResultGraphQLQuery.Data> apolloCall = this.indicoClient.apolloClient.query(JobResultGraphQLQuery.builder()
                .id(this.id)
                .build());
        Response<JobResultGraphQLQuery.Data> response = Async.executeSync(apolloCall).get();
        JobResultGraphQLQuery.Data data = response.data();

        JobResultGraphQLQuery.Job job = data.job();
        JobStatus status = job.status();
        if (status != JobStatus.SUCCESS) {
            throw new RuntimeException("Job finished with status : " + status.rawValue());
        }

        Object result = job.result();
        if (null == result) {
            throw new RuntimeException("Job has finished with no results");
        }

        String out = result.toString();
        return out;
        }catch (CompletionException | ExecutionException | InterruptedException ex){
            throw new RuntimeException("Call for the job result failed", ex);
        }
    }

    /**
     * If job status is FAILURE returns the list of errors encoutered
     *
     * @return List of Errors
     */
    public List<String> errors() {
        //TODO:
        return new ArrayList<String>();
    }
}
