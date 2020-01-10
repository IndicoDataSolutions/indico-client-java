package com.indico.workflows;

import java.util.List;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;

import com.indico.Async;
import com.indico.Mutation;
import com.indico.PredictGraphQLMutation;
import com.indico.jobs.Job;
import com.indico.jobs.JobOptions;

public class ModelGroupSubmission implements Mutation<Job> {

    private int modelId;
    private List<String> data;
    private JobOptions jobOptions;
    private final ApolloClient apolloClient;
    
    public ModelGroupSubmission(ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
    }

    public ModelGroupSubmission modelGroup(ModelGroup modelGroup) {
        modelId = modelGroup.selectedModel.id;
        return this;
    }

    public ModelGroupSubmission modelId(int modelId) {
        this.modelId = modelId;
        return this;
    }

    public ModelGroupSubmission data(List<String> data) {
        this.data = data;
        return this;
    }

    public ModelGroupSubmission jobOptions(JobOptions jobOptions) {
        this.jobOptions = jobOptions;
        return this;
    }

    @Override
    public Job execute() {
        ApolloCall<PredictGraphQLMutation.Data> apolloCall = this.apolloClient.mutate(PredictGraphQLMutation.builder()
                .modelId(modelId)
                .data(data)
                .build());
        Response<PredictGraphQLMutation.Data> response = (Response<PredictGraphQLMutation.Data>) Async.executeSync(apolloCall).join();
        String jobId = response.data().modelPredict().jobId();
        return new Job(this.apolloClient,jobId);
    }
}
