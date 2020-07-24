package com.indico.entity;

import com.indico.type.SubmissionStatus;

public class Submission {

    public final int id;
    public final int datasetId;
    public final int workflowId;
    public final SubmissionStatus status;
    public final String inputFile;
    public final String inputFilename;
    public final String resultFile;
    public final boolean retrieved;

    private Submission(Builder builder) {
        this.id = builder.id;
        this.datasetId = builder.datasetId;
        this.workflowId = builder.workflowId;
        this.status = builder.status;
        this.inputFile = builder.inputFile;
        this.inputFilename = builder.inputFilename;
        this.resultFile = builder.resultFile;
        this.retrieved = builder.retrieved;
    }

    public static class Builder {
        protected int id;
        protected int datasetId;
        protected int workflowId;
        protected SubmissionStatus status;
        protected String inputFile;
        protected String inputFilename;
        protected String resultFile;
        protected boolean retrieved = false;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder datasetId(int datasetId) {
            this.datasetId = datasetId;
            return this;
        }

        public Builder workflowId(int workflowId) {
            this.workflowId = workflowId;
            return this;
        }

        public Builder status(SubmissionStatus status) {
            this.status = status;
            return this;
        }

        public Builder inputFile(String inputFile) {
            this.inputFile = inputFile;
            return this;
        }

        public Builder inputFilename(String inputFilename) {
            this.inputFilename = inputFilename;
            return this;
        }

        public Builder resultFile(String resultFile) {
            this.resultFile = resultFile;
            return this;
        }

        public Builder retrieved(boolean retrieved) {
            this.retrieved = retrieved;
            return this;
        }

        public Submission build() {
            return new Submission(this);
        }
    }
}
