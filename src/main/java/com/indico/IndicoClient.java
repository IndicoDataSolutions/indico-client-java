package com.indico;

import java.util.ArrayList;
import java.util.List;

import com.indico.jobs.JobQuery;
import com.indico.storage.RetrieveBlob;
import com.indico.storage.PurgeBlob;
import com.indico.workflows.WorkflowQuery;
import com.indico.workflows.WorkflowSubmission;
import com.indico.IndicoConfig;

/**
 * Indico client with all available top level query and mutations
 */
public class IndicoClient {
    public final IndicoConfig config;

    public IndicoClient(IndicoConfig config) {
        this.config = config;
    }

    /**
     * Create a new query for a workflow
     * @return WorkflowQuery
     */
    public WorkflowQuery workflowQuery() {
        return new WorkflowQuery();
    }

    /**
     * Create a new mutation to submit documents to process by a workflow
     * @return WorkflowSubmission
     */
    public WorkflowSubmission workflowSubmission() {
        return new WorkflowSubmission();
    }

    /**
     * Create a query to retrieve async job info
     * @return JobQuery
     */
    public JobQuery jobQuery() {
        return new JobQuery();
    }

    /**
     * Retrieve a blob from indico blob storage
     * @return RetrieveBlob
     */
    public RetrieveBlob retrieveBlob() {
        return new RetrieveBlob();
    }

    /**
     * Create a request to delete a blob fron indico blob storage
     * @return PurgeBlob
     */
    public PurgeBlob purgeBlob() {
        return new PurgeBlob();
    }
}