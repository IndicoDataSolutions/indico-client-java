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

public class ListWorkflows implements Query<List<Workflow>> {

    private List<Integer> datasetIds;
    private List<Integer> workflowIds;
    private final IndicoClient client;

    public ListWorkflows(IndicoClient client) {
        this.client = client;
    }

    /**
     * List of dataset ids to filter by
     * @param ids Dataset Ids
     * @return ListWorkflows
     */
    public ListWorkflows datasetIds(List<Integer> ids) {
        this.datasetIds = ids;
        return this;
    }

    /**
     * List of workflow ids to filter by
     * @param ids
     * @return ListWorkflows
     */
    public ListWorkflows workflowIds(List<Integer> ids) {
        this.workflowIds = ids;
        return this;
    }

    /**
     * Queries the server and returns Workflow List
     * @return Workflow List
     */
    @Override
    public List<Workflow> query() {
        ApolloCall<ListWorkflowsGraphQLQuery.Data> apolloCall = this.client.apolloClient.query(ListWorkflowsGraphQLQuery.builder()
                .datasetIds(this.datasetIds)
                .workflowIds(this.workflowIds)
                .build());
        Response<ListWorkflowsGraphQLQuery.Data> response = (Response<ListWorkflowsGraphQLQuery.Data>) Async.executeSync(apolloCall, this.client.config).join();

        List<ListWorkflowsGraphQLQuery.Workflow> wf = response.data().workflows().workflows();
        List<Workflow> workflows = new ArrayList<>();
        wf.forEach(workflow -> workflows.add(new Workflow.Builder()
                .id(workflow.id())
                .name(workflow.name())
                .reviewEnabled(workflow.reviewEnabled())
                .build()));
        return workflows;
    }

    @Override
    public List<Workflow> refresh(List<Workflow> obj) {
        return obj;
    }
}
