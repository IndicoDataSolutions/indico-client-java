package com.indico.jobs;

import com.apollographql.apollo.ApolloClient;
import java.util.List;

import com.indico.IndicoClient;
import com.indico.Query;

public class JobQuery implements Query<Job> {

    private String id;
    private final IndicoClient indicoClient;

    public JobQuery(IndicoClient indicoClient) {
        this.indicoClient = indicoClient;
    }

    /**
     * Use to query job by id
     *
     * @param id job id
     * @return JobQuery
     */
    public JobQuery id(String id) {
        this.id = id;

        return this;
    }

    /**
     * Returns Job
     * 
     * @return Job 
     */
    public Job query() {
        return new Job(this.indicoClient, this.id);
    }

    /**
     * Refresh the Job
     * 
     * @param obj Job
     * @return Job
     */
    public Job refresh(Job obj) {
        return obj;
    }
}
