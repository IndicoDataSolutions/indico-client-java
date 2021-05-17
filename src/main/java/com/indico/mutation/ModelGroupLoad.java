package com.indico.mutation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;

import com.indico.Async;
import com.indico.IndicoClient;
import com.indico.Mutation;
import com.indico.LoadModelGraphQLMutation;
import com.indico.entity.ModelGroup;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class ModelGroupLoad implements Mutation<String> {

    private int modelId;
    private final IndicoClient indicoClient;
    private final int maxRetries;

    public ModelGroupLoad(IndicoClient indicoClient) {
        this.indicoClient = indicoClient;
        this.maxRetries = this.indicoClient.config.maxRetries;
    }

    /**
     * Use to load ModelGroup
     * 
     * @param modelGroup ModelGroup
     * @return ModelGroupLoad
     */
    public ModelGroupLoad modelGroup(ModelGroup modelGroup) {
        modelId = modelGroup.selectedModel.id;
        return this;
    }

    /**
     * Use to load ModelGroup by id
     * 
     * @param modelId Model id
     * @return ModelGroupLoad
     */
    public ModelGroupLoad modelId(int modelId) {
        this.modelId = modelId;
        return this;
    }

    /**
     * Executes request and returns load status 
     * 
     * @return Load status
     */
    @Override
    public String execute() {
        try {
            ApolloCall<LoadModelGraphQLMutation.Data> apolloCall = this.indicoClient.apolloClient.mutate(LoadModelGraphQLMutation.builder()
                    .model_id(modelId)
                    .build());
            Response<LoadModelGraphQLMutation.Data> response = Async.executeSync(apolloCall).get();
            String status = response.data().modelLoad().status();
            return status;

        } catch (CompletionException | ExecutionException | InterruptedException ex) {
            throw new RuntimeException("Call to load model group failed", ex);
        }
    }
}
