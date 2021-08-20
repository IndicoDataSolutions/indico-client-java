package com.indico.query;


import com.expediagroup.graphql.client.types.GraphQLClientResponse;
import com.indico.IndicoKtorClient;
import com.indico.graphql.listsubmissionsgraphql.Submission;
import com.indico.graphql.ListSubmissionsGraphQL;
import com.indico.Query;
import com.indico.graphql.inputs.SubmissionFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;


public class ListSubmissions implements Query<List<com.indico.entity.Submission>> {
    private final IndicoKtorClient client;
    private List<Integer> submissionIds;
    private List<Integer> workflowIds;
    private SubmissionFilter filters;
    private int limit = 1000;

    public ListSubmissions(IndicoKtorClient client) {
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
    public List<com.indico.entity.Submission> query() {
        try{
            ListSubmissionsGraphQL.Variables variables = new ListSubmissionsGraphQL.Variables(this.submissionIds, this.workflowIds, this.filters, this.limit);

            ListSubmissionsGraphQL listSubmissionsGraphQL = new ListSubmissionsGraphQL(variables);
            GraphQLClientResponse<ListSubmissionsGraphQL.Result> result = this.client.execute(listSubmissionsGraphQL);


        List<Submission> submissionList = result.getData().getSubmissions().getSubmissions();
        ArrayList<com.indico.entity.Submission> submissions = new ArrayList<>();
        submissionList.forEach(submission -> submissions.add(new com.indico.entity.Submission.Builder()
                .id(submission.getId())
                .datasetId(submission.getDatasetId())
                .workflowId(submission.getWorkflowId())
                .status(submission.getStatus())
                .inputFile(submission.getInputFile())
                .inputFilename(submission.getInputFilename())
                .resultFile(submission.getResultFile())
                .build()));
        return submissions;
        }catch (CompletionException ex){
            throw new RuntimeException("Call to list the submissions failed", ex);
        }
    }

    /**
     * Refresh the Submission List
     * @param obj
     * @return Submission List
     */
    @Override
    public List<com.indico.entity.Submission> refresh(List<com.indico.entity.Submission> obj) { return obj; }
}
