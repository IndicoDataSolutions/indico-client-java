package com.indico.workflows;

import java.io.BufferedOutputStream;
import java.nio.*;
import com.indico.Mutation;
import com.indico.jobs.Job;
import com.indico.jobs.JobOptions;


public class WorkflowSubmission implements Mutation<Job> {
    private int workflow_id; 
    private String filepath;
    private String data;
    private BufferedOutputStream dataStream;
    private JobOptions jobOptions;

    public WorkflowSubmission workflow(Workflow wf) {
        workflow_id = wf.id;
        return this;
    }

    public WorkflowSubmission workflow_id(int workflow_id) {
        this.workflow_id = workflow_id;
        return this;
    }

    public WorkflowSubmission filepath(String filepath) {
        this.filepath = filepath;
        return this;
    }

    public WorkflowSubmission dataStream(BufferedOutputStream dataStream) {
        this.dataStream = dataStream;
        return this;
    }

    public WorkflowSubmission data(String data) {
        this.data = data;
        return this;
    }

    public WorkflowSubmission jobOptions(JobOptions jobOptions) {
        this.jobOptions = jobOptions;
        return this;
    }

    public Job execute() {
        return new Job();
    }
}