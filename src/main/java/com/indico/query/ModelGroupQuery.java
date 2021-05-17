package com.indico.query;

import com.indico.IndicoClient;
import com.indico.entity.ModelGroup;
import java.util.ArrayList;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;

import com.indico.Query;
import com.indico.Async;
import com.indico.ModelGroupGraphQLQuery;
import com.indico.entity.Model;

public class ModelGroupQuery implements Query<ModelGroup> {

    private int id;
    private String name;
    private final IndicoClient indicoClient;

    public ModelGroupQuery(IndicoClient indicoClient) {
        this.indicoClient = indicoClient;
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
        try{
        ArrayList<Integer> modelGroupIds = new ArrayList<>();
        modelGroupIds.add(this.id);
        ApolloCall<ModelGroupGraphQLQuery.Data> apolloCall = this.indicoClient.apolloClient.query(ModelGroupGraphQLQuery.builder()
                .modelGroupIds(modelGroupIds)
                .build());
        Response<ModelGroupGraphQLQuery.Data> response = Async.executeSync(apolloCall).get();
        ModelGroupGraphQLQuery.ModelGroup mg = response.data().modelGroups().modelGroups().get(0);
        Model model = new Model.Builder()
                .id(mg.selectedModel().id())
                .status(mg.selectedModel().status())
                .build();
        return new ModelGroup.Builder()
                .id(mg.id())
                .name(mg.name())
                .status(mg.status())
                .selectedModel(model)
                .build();
        }catch (CompletionException | ExecutionException | InterruptedException ex){
            throw new RuntimeException("Call to generate the submission result failed", ex);
        }
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
