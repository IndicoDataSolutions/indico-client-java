package com.indico.query;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.indico.Async;
import com.indico.IndicoClient;
import com.indico.ModelGroupProgressGraphQLQuery;
import com.indico.Query;
import com.indico.entity.Model;
import com.indico.entity.TrainingProgress;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TrainingModelWithProgressQuery implements Query<Model> {

    private int id;
    private String name;
    private final IndicoClient client;

    public TrainingModelWithProgressQuery(IndicoClient client) {
        this.client = client;
    }

    /**
     * Use to query TrainingModelWithProgress by id
     *
     * @param id
     * @return TrainingModelWithProgressQuery
     */
    public TrainingModelWithProgressQuery id(int id) {
        this.id = id;
        return this;
    }

    /**
     * Use to query TrainingModelWithProgress by name
     *
     * @param name
     * @return TrainingModelWithProgressQuery
     */
    public TrainingModelWithProgressQuery name(String name) {
        this.name = name;
        return this;
    }

    /**
     * Queries the server and returns Training Model
     *
     * @return Model
     */
    @Override
    public Model query() {
        ApolloCall<ModelGroupProgressGraphQLQuery.Data> apolloCall = this.client.apolloClient.query(ModelGroupProgressGraphQLQuery.builder()
                .id(this.id)
                .build());
        Response<ModelGroupProgressGraphQLQuery.Data> response = (Response<ModelGroupProgressGraphQLQuery.Data>) Async.executeSync(apolloCall).join();
        List<ModelGroupProgressGraphQLQuery.ModelGroup> modelGroups = response.data().modelGroups().modelGroups();
        if (modelGroups.size() != 1) {
            throw new RuntimeException("Cannot find Model Group");
        }
        List<ModelGroupProgressGraphQLQuery.Model> models = modelGroups.get(0).models();
        ModelGroupProgressGraphQLQuery.Model model = Collections.max(models, Comparator.comparingInt(m -> m.id()));
        TrainingProgress progress = new TrainingProgress(model.trainingProgress().percentComplete());
        return new Model.Builder()
                .id(model.id())
                .status(model.status())
                .trainingProgress(progress)
                .build();
    }
}
