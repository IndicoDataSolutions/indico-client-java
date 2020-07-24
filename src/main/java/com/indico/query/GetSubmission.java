package com.indico.query;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.indico.Async;
import com.indico.GetSubmissionGraphQLQuery;
import com.indico.IndicoClient;
import com.indico.Query;
import com.indico.entity.Submission;

public class GetSubmission implements Query<Submission> {
    private final IndicoClient client;
    private int submissionId;

    public GetSubmission(IndicoClient client) {
        this.client = client;
    }

    /**
     * Submission Id
     * @param id submissionId
     * @return GetSubmission
     */
    public GetSubmission submissionId(int id) {
        this.submissionId = id;
        return this;
    }

    /**
     * Execute the query
     * @return Submission
     */
    @Override
    public Submission query() {
        ApolloCall<GetSubmissionGraphQLQuery.Data> apolloCall = this.client.apolloClient.query(GetSubmissionGraphQLQuery.builder()
                .submissionId(this.submissionId)
                .build());

        Response<GetSubmissionGraphQLQuery.Data> response = (Response<GetSubmissionGraphQLQuery.Data>) Async.executeSync(apolloCall).join();
        GetSubmissionGraphQLQuery.Submission submission = response.data().submission();
        return new Submission.Builder()
                .id(submission.id())
                .datasetId(submission.datasetId())
                .workflowId(submission.workflowId())
                .status(submission.status())
                .inputFile(submission.inputFile())
                .inputFilename(submission.inputFilename())
                .resultFile(submission.resultFile())
                .retrieved(submission.retrieved())
                .build();
    }

    /**
     * Refresh the Submission
     * @param obj
     * @return Submission
     */
    @Override
    public Submission refresh(Submission obj) { return obj; }
}
