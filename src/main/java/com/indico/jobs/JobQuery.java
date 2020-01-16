package com.indico.jobs;

import com.apollographql.apollo.ApolloClient;
import java.util.List;

import com.indico.Query;

public class JobQuery implements Query<Job> {

    private String id;
    private final ApolloClient apolloClient;

    public JobQuery(ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
    }

    public JobQuery id(String id) {
        this.id = id;

        return this;
    }

    public Job query() {
        return new Job(this.apolloClient, this.id);
    }

    public Job refresh(Job obj) {
        return obj;
    }
}
