package com.indico.query;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.indico.Async;
import com.indico.GetSubmissionGraphQLQuery;
import com.indico.IndicoClient;
import com.indico.Query;
import com.indico.entity.Submission;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class GetSubmission implements Query<Submission> {
    private final IndicoClient client;
    private int submissionId;
    private final int maxRetries;

    public GetSubmission(IndicoClient client) {
        this.client = client;
        maxRetries = this.client.config.maxRetries;
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
        try{
        ApolloCall<GetSubmissionGraphQLQuery.Data> apolloCall = this.client.apolloClient.query(GetSubmissionGraphQLQuery.builder()
                .submissionId(this.submissionId)
                .build());

        Response<GetSubmissionGraphQLQuery.Data> response = Async.executeSync(apolloCall).get();
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
        }catch (CompletionException | ExecutionException | InterruptedException ex){
            throw new RuntimeException("Call to get the submission failed", ex);
        }
    }

    /**
     * Refresh the Submission
     * @param obj
     * @return Submission
     */
    @Override
    public Submission refresh(Submission obj) { return obj; }
}
