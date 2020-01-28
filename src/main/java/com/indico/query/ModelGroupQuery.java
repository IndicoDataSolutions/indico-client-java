package com.indico.query;

import com.indico.entity.ModelGroup;
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
     * Use to query ModelGroup by id
     *
     * @param id
     * @return ModelGroupQuery
     */
    public ModelGroupQuery id(int id) {
        this.id = id;
        return this;
    }

    /**
     * Use to query ModelGroup by name
     *
     * @param name
     * @return ModelGroupQuery
     */
    public ModelGroupQuery name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Queries the server and returns ModelGroup
     * 
     * @return ModelGroup
     */
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

    /**
     * Refreshes the ModelGroup Object
     * 
     * @param obj ModelGroup
     * @return ModelGroup
     */
    @Override
    public ModelGroup refresh(ModelGroup obj) {
        return obj;
    }
}
