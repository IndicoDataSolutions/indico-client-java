package com.indico.query;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.indico.Async;
import com.indico.IndicoClient;
import com.indico.ListSubmissionsGraphQLQuery;
import com.indico.Query;
import com.indico.entity.Submission;
import com.indico.type.SubmissionFilter;

import java.util.ArrayList;
import java.util.List;

public class ListSubmissions implements Query<List<Submission>> {
    private final IndicoClient client;
    private List<Integer> submissionIds;
    private List<Integer> workflowIds;
    private SubmissionFilter filters;
    private int limit = 1000;

    public ListSubmissions(IndicoClient client) {
        this.client = client;
    }

    /**
     * Submission ids to filter by
     * @param ids submissionIds
     * @return ListSubmissions
     */
    public ListSubmissions submissionIds(List<Integer> ids) {
        this.submissionIds = ids;
        return this;
    }

    /**
     * Workflow ids to filter by
     * @param ids workflowIds
     * @return ListSubmissions
     */
    public ListSubmissions workflowIds(List<Integer> ids) {
        this.workflowIds = ids;
        return this;
    }

    /**
     * Submission attributes to filter by
     * @param filters submission filter
     * @return ListSubmissions
     */
    public ListSubmissions filters(SubmissionFilter filters) {
        this.filters = filters;
        return this;
    }

    /**
     * Maximum number of Submissions to return. Defaults to 1000
     * @param limit
     * @return ListSubmissions
     */
    public ListSubmissions limit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * Execute the query
     * @return Submission List
     */
    @Override
    public List<Submission> query() {
        ApolloCall<ListSubmissionsGraphQLQuery.Data> apolloCall = this.client.apolloClient.query(ListSubmissionsGraphQLQuery.builder()
                .submissionIds(this.submissionIds)
                .workflowIds(this.workflowIds)
                .filters(this.filters)
                .limit(this.limit)
                .build());

        Response<ListSubmissionsGraphQLQuery.Data> response = (Response<ListSubmissionsGraphQLQuery.Data>) Async.executeSync(apolloCall, this.client.config).join();

        List<ListSubmissionsGraphQLQuery.Submission> submissionList = response.data().submissions().submissions();
        ArrayList<Submission> submissions = new ArrayList<>();
        submissionList.forEach(submission -> submissions.add(new Submission.Builder()
                .id(submission.id())
                .datasetId(submission.datasetId())
                .workflowId(submission.workflowId())
                .status(submission.status())
                .inputFile(submission.inputFile())
                .inputFilename(submission.inputFilename())
                .resultFile(submission.resultFile())
                .build()));
        return submissions;
    }

    /**
     * Refresh the Submission List
     * @param obj
     * @return Submission List
     */
    @Override
    public List<Submission> refresh(List<Submission> obj) { return obj; }
}
