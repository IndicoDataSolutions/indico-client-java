package com.indico.mutation;

import com.indico.IndicoClient;
import com.indico.Mutation;
import com.indico.entity.Submission;
import com.indico.jobs.Job;
import com.indico.query.GetSubmission;
import com.indico.type.SubmissionStatus;

public class SubmissionResult implements Mutation<Job> {
    private final IndicoClient client;
    private int submissionId;
    private SubmissionStatus checkStatus;

    public SubmissionResult(IndicoClient client) {
        this.client = client;
    }

    /**
     * Id of the submission
     * @param submissionId submission Id
     * @return SubmissionResult
     */
    public SubmissionResult submission(int submissionId) {
        this.submissionId = submissionId;
        return this;
    }

    public SubmissionResult submission(Submission submission) {
        this.submissionId = submission.id;
        return this;
    }

    public SubmissionResult checkStatus(SubmissionStatus checkStatus) {
        this.checkStatus = checkStatus;
        return this;
    }

    /**
     * Generate a result file for a Submission
     * @return Job
     */
    @Override
    public Job execute() {
        GetSubmission getSubmission = new GetSubmission(this.client).submissionId(this.submissionId);
        Submission submission = getSubmission.query();
        while(!statusCheck(submission.status)) {
            submission = getSubmission.query();
            try {
                Thread.sleep(1000);
            } catch(InterruptedException exc) {
                throw new RuntimeException(exc.getMessage(), exc.fillInStackTrace());
            }
        }

        if(!statusCheck(submission.status)) {
            throw new RuntimeException("Request timed out");
        }
        else if(submission.status.equals(SubmissionStatus.$UNKNOWN)) {
            throw new RuntimeException("Submission " + this.submissionId + " does not meet status requirements");
        }

        GenerateSubmissionResult generateSubmissionResult = new GenerateSubmissionResult(this.client)
                .submission(submission);
        Job job = generateSubmissionResult.execute();
        return job;
    }

    private boolean statusCheck(SubmissionStatus status) {
        if(checkStatus != null) {
            return status.equals(checkStatus);
        }
        return !status.equals(SubmissionStatus.PROCESSING);
    }
}
