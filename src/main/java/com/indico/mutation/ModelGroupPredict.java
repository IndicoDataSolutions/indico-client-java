package com.indico.mutation;

import java.util.List;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.api.Response;

import com.indico.jobs.Job;
import com.indico.jobs.JobOptions;
import com.indico.Async;
import com.indico.Mutation;
import com.indico.entity.ModelGroup;
import com.indico.PredictModelGraphQLMutation;

public class ModelGroupPredict implements Mutation<Job> {

    private int modelId;
    private List<String> data;
    private JobOptions jobOptions;
    private final ApolloClient apolloClient;
    
    public ModelGroupPredict(ApolloClient apolloClient) {
        this.apolloClient = apolloClient;
    }

    /**
     * Use to predict ModelGroup
     * 
     * @param modelGroup ModelGroup
     * @return ModelGroupPredict
     */
    public ModelGroupPredict modelGroup(ModelGroup modelGroup) {
        modelId = modelGroup.selectedModel.id;
        return this;
    }

    /**
     * Use to predict ModelGroup by id
     * 
     * @param modelId Model id
     * @return ModelGroupPredict
     */
    public ModelGroupPredict modelId(int modelId) {
        this.modelId = modelId;
        return this;
    }

    /**
     * Data to predict
     * 
     * @param data Data
     * @return ModelGroupPredict
     */
    public ModelGroupPredict data(List<String> data) {
        this.data = data;
        return this;
    }

    /**
     * Job Options for Job
     * 
     * @param jobOptions Job options
     * @return ModeGroupPredict
     */
    public ModelGroupPredict jobOptions(JobOptions jobOptions) {
        this.jobOptions = jobOptions;
        return this;
    }

    /**
     * Executes request and returns job 
     * 
     * @return Job
     */
    @Override
    public Job execute() {
        ApolloCall<PredictModelGraphQLMutation.Data> apolloCall = this.apolloClient.mutate(PredictModelGraphQLMutation.builder()
                .modelId(modelId)
                .data(data)
                .build());
        Response<PredictModelGraphQLMutation.Data> response = (Response<PredictModelGraphQLMutation.Data>) Async.executeSync(apolloCall).join();
        String jobId = response.data().modelPredict().jobId();
        return new Job(this.apolloClient,jobId);
    }
}
