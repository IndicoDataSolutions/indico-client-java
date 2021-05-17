package com.indico.mutation;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.indico.Async;
import com.indico.CreateSubmissionResultsGraphQLMutation;
import com.indico.IndicoClient;
import com.indico.Mutation;
import com.indico.entity.Submission;
import com.indico.jobs.Job;

import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class GenerateSubmissionResult implements Mutation<Job> {
    private final IndicoClient client;
    private int submissionId;
    private boolean retrieved;

    public GenerateSubmissionResult(IndicoClient client) {
        this.client = client;
    }

    public GenerateSubmissionResult submission(int submissionId) {
        this.submissionId = submissionId;
        return this;
    }

    public GenerateSubmissionResult submission(Submission submission) {
        this.submissionId = submission.id;
        return this;
    }

    @Override
    public Job execute() {
        try{
        ApolloCall<CreateSubmissionResultsGraphQLMutation.Data> apolloCall = this.client.apolloClient.mutate(CreateSubmissionResultsGraphQLMutation.builder()
                .submissionId(this.submissionId)
                .build());

        Response<CreateSubmissionResultsGraphQLMutation.Data> response = Async.executeSync(apolloCall).get();
        CreateSubmissionResultsGraphQLMutation.SubmissionResults submissionResults = response.data().submissionResults();
        String jobId = submissionResults.jobId();
        return new Job(this.client, jobId);  }
        catch (CompletionException | ExecutionException | InterruptedException ex){
            throw new RuntimeException("Call to generate the submission result failed", ex);
        }
    }
}
