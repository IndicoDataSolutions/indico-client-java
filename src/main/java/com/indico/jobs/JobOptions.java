package com.indico.jobs;

/**
 * Configuration parameters to modify how async jobs are handled
 */
public class JobOptions {
    public final int priority;

    public static class Builder {
        private int priority = 2;
        
        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public JobOptions build() {
            return new JobOptions(this);
        }
    }

    private JobOptions(Builder builder) {
        this.priority = builder.priority;
    }
}