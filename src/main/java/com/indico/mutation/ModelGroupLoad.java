package com.indico.mutation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;

import com.indico.Async;
import com.indico.Mutation;
import com.indico.LoadModelGraphQLMutation;
import com.indico.entity.ModelGroup;

public class ModelGroupLoad implements Mutation<String> {

    private int modelId;
    private final ApolloClient apolloClient;

    public ModelGroupLoad(ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
    }

    public ModelGroupLoad modelGroup(ModelGroup modelGroup) {
        modelId = modelGroup.selectedModel.id;
        return this;
    }

    public ModelGroupLoad modelId(int modelId) {
        this.modelId = modelId;
        return this;
    }

    @Override
    public String execute() {
        ApolloCall<LoadModelGraphQLMutation.Data> apolloCall = this.apolloClient.mutate(LoadModelGraphQLMutation.builder()
                .model_id(modelId)
                .build());
        Response<LoadModelGraphQLMutation.Data> response = (Response<LoadModelGraphQLMutation.Data>) Async.executeSync(apolloCall).join();
        String status = response.data().modelLoad().status();
        return status;
    }
}
