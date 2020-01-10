package com.indico.workflows;

import java.util.ArrayList;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;

import com.indico.Query;
import com.indico.Async;
import com.indico.ModelGroupGraphQLQuery;

public class ModelGroupQuery implements Query<ModelGroup> {

    private int id;
    private String name;
    private final ApolloClient apolloClient;

    public ModelGroupQuery(ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
    }

    /**
     * Use to query workflow by id
     *
     * @param id
     * @return WorkflowQuery
     */
    public ModelGroupQuery id(int id) {
        this.id = id;
        return this;
    }

    /**
     * Use to query workflow by name
     *
     * @param name
     * @return WorkflowQuery
     */
    public ModelGroupQuery name(String name) {
        this.name = name;
        return this;
    }

    @Override
    public ModelGroup query() {
        ArrayList<Integer> modelGroupIds = new ArrayList<>();
        modelGroupIds.add(this.id);
        ApolloCall<ModelGroupGraphQLQuery.Data> apolloCall = this.apolloClient.query(ModelGroupGraphQLQuery.builder()
                .modelGroupIds(modelGroupIds)
                .build());
        Response<ModelGroupGraphQLQuery.Data> response = (Response<ModelGroupGraphQLQuery.Data>) Async.executeSync(apolloCall).join();
        return new ModelGroup(response.data().modelGroups().modelGroups().get(0));
    }

    @Override
    public ModelGroup refresh(ModelGroup obj) {
        return obj;
    }
}
