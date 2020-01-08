package com.indico.api;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import java.util.ArrayList;
import com.indico.InfoQuery;
import com.indico.SelectedModelQuery;
import com.indico.LoadMutation;
import com.indico.PredictMutation;
import com.indico.graphql.GraphQL;
import com.indico.jobs.Job;
import java.util.List;
import org.json.JSONObject;

public class ModelGroup {

    private final ApolloClient apolloClient;
    private final int id;
    
    private ModelGroup(Builder builder) {
        this.apolloClient = builder.apolloClient;
        this.id = builder.id;
    }

    /**
     * Returns a Job for predicting data for the specific model
     *
     * @param data list of string to predict
     * @param modelId model id
     * @return instance of Job for async results
     */
    public Job predict(List<String> data, int modelId) {
        ApolloCall< PredictMutation.Data> apolloCall = this.apolloClient.mutate(PredictMutation.builder()
                .modelId(modelId)
                .data(data)
                .build());
        Response<PredictMutation.Data> response = (Response<PredictMutation.Data>) GraphQL.execute(apolloCall).join();
        String jobId = response.data().modelPredict().jobId();
        Job job = new Job(jobId, this.apolloClient);
        return job;
    }

    /**
     * Returns a Job for predicting data for selected model
     *
     * @param data list of string to predict
     * @return instance of Job for async results
     */
    public Job predict(List<String> data) {
        int modelId = this.getSelectedModel().getInt("id");
        return this.predict(data, modelId);
    }

    /**
     * Loads specified model
     *
     * @param modelId model id
     * @return load status
     */
    public String load(int modelId) {
        JSONObject info = this.info();
        if (info.has("load_status") && info.getString("load_status").equals("ready")) {
            return "ready";
        }

        ApolloCall<LoadMutation.Data> apolloCall = this.apolloClient.mutate(LoadMutation.builder()
                .model_id(modelId)
                .build());
        Response<LoadMutation.Data> response = (Response<LoadMutation.Data>) GraphQL.execute(apolloCall).join();
        LoadMutation.Data data = response.data();
        if (data.modelLoad() == null) {
            throw new RuntimeException("Cannot Load Model id : " + modelId);
        }
        return this.getStatus(data);
    }

    /**
     * Loads selected model
     *
     * @return load status
     */
    public String load() {
        int modelId = this.getSelectedModel().getInt("id");
        return load(modelId);
    }

    private String getStatus(LoadMutation.Data data) {
        JSONObject info;
        LoadMutation.ModelLoad modelLoad = data.modelLoad();
        String status = modelLoad.status();

        while (status.equals("loading")) {
            info = this.info();
            if (!info.has("load_status")) {
                status = "loading";
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            } else {
                status = info.getString("load_status");
            }
        }

        return status;
    }

    /**
     * returns json containing info about the selected model
     *
     * @return modelInfo as JSONObject
     */
    public JSONObject info() {
        ArrayList<Integer> modelGroupIds = new ArrayList<Integer>();
        modelGroupIds.add(this.id);
        ApolloCall<InfoQuery.Data> apolloCall = this.apolloClient.query(InfoQuery.builder()
                .modelGroupIds(modelGroupIds)
                .build());
        Response<InfoQuery.Data> response = (Response<InfoQuery.Data>) GraphQL.execute(apolloCall).join();
        InfoQuery.Data data = response.data();
        List<InfoQuery.ModelGroup> modelGroupList = data.modelGroups().modelGroups();
        if (modelGroupList.isEmpty()) {
            throw new RuntimeException("Cannot find the default selected model for model group : " + this.id);
        }
        Object model = modelGroupList.get(0).selectedModel().modelInfo();
        return new JSONObject(model.toString());
    }

    /**
     * returns json containing selected model
     *
     * @return selected model as JSONObject
     */
    public JSONObject getSelectedModel() {
        ArrayList<Integer> modelGroupIds = new ArrayList<Integer>();
        modelGroupIds.add(this.id);
        ApolloCall<SelectedModelQuery.Data> apolloCall = this.apolloClient.query(SelectedModelQuery.builder()
                .modelGroupIds(modelGroupIds)
                .build());
        Response<SelectedModelQuery.Data> response = (Response<SelectedModelQuery.Data>) GraphQL.execute(apolloCall).join();
        SelectedModelQuery.Data data = response.data();
        List<SelectedModelQuery.ModelGroup> modelGroupList = data.modelGroups().modelGroups();
        if (modelGroupList.isEmpty()) {
            throw new RuntimeException("Cannot find the default selected model for model group : " + this.id);
        }
        SelectedModelQuery.SelectedModel model = modelGroupList.get(0).selectedModel();
        JSONObject json = new JSONObject();
        json.put("id", model.id());
        json.put("modelInfo", new JSONObject(model.modelInfo().toString()));
        return json;
    }

    public static class Builder {

        protected int id = 0;
        protected final ApolloClient apolloClient;
        
        public Builder(ApolloClient apolloClient) {
            this.apolloClient = apolloClient;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public ModelGroup build() {
            if (id == 0) {
                throw new RuntimeException("ModelGroup Id is Required");
            }
            return new ModelGroup(this);
        }
    }
}
