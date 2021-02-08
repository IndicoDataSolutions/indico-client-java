package com.indico.mutation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.indico.Async;
import com.indico.IndicoClient;
import com.indico.Mutation;
import com.indico.UpdateSubmissionGraphQLMutation;
import com.indico.entity.Submission;

public class UpdateSubmission implements Mutation<Submission> {
    private final IndicoClient client;
    private int submissionId;
    private boolean retrieved;

    public UpdateSubmission(IndicoClient client) {
        this.client = client;
    }

    /**
     * Id of the submission to update
     * @param id submissionId
     * @return UpdateSubmission
     */
    public UpdateSubmission submissionId(int id) {
        this.submissionId = id;
        return this;
    }

    /**
     * Mark the submission as having been retrieved
     * @param retrieved retrieved
     * @return UpdateSubmission
     */
    public UpdateSubmission retrieved(boolean retrieved) {
        this.retrieved = retrieved;
        return this;
    }

    /**
     * Update properties of the Submission object
     * @return Submission
     */
    @Override
    public Submission execute() {
        ApolloCall<UpdateSubmissionGraphQLMutation.Data> apolloCall = this.client.apolloClient.mutate(UpdateSubmissionGraphQLMutation.builder()
                .submissionId(this.submissionId)
                .retrieved(this.retrieved)
                .build());

        Response<UpdateSubmissionGraphQLMutation.Data> response = (Response<UpdateSubmissionGraphQLMutation.Data>) Async.executeSync(apolloCall, this.client.config).join();
        UpdateSubmissionGraphQLMutation.UpdateSubmission submission = response.data().updateSubmission();
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
}
