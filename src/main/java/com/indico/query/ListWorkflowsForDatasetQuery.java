package com.indico.query;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.indico.Async;
import com.indico.IndicoClient;
import com.indico.ListWorkflowsGraphQLQuery;
import com.indico.Query;
import com.indico.entity.Workflow;
import java.util.ArrayList;
import java.util.List;

public class ListWorkflowsForDatasetQuery implements Query<List<Workflow>> {

    private int datasetId;
    private final IndicoClient client;

    public ListWorkflowsForDatasetQuery(IndicoClient client) {
        this.client = client;
    }

    public ListWorkflowsForDatasetQuery datasetId(int id) {
        this.datasetId = id;
        return this;
    }

    @Override
    public List<Workflow> query() {
        ApolloCall<ListWorkflowsGraphQLQuery.Data> apolloCall = this.client.apolloClient.query(ListWorkflowsGraphQLQuery.builder()
                .datasetId(this.datasetId)
                .build());
        Response<ListWorkflowsGraphQLQuery.Data> response = (Response<ListWorkflowsGraphQLQuery.Data>) Async.executeSync(apolloCall).join();
        List<ListWorkflowsGraphQLQuery.Workflow> wf = response.data().workflows().workflows();
        List<Workflow> workflows = new ArrayList<>();
        wf.forEach(workflow -> workflows.add(new Workflow.Builder()
                .id(workflow.id())
                .name(workflow.name())
                .build()));
        return workflows;
    }

    @Override
    public List<Workflow> refresh(List<Workflow> obj) {
        return obj;
    }
}
