package com.indico;

public class Enums {

    public enum JobStatus {
        /**
         * Task state is unknown (assumed pending since you know the id).
         */
        PENDING,
        /**
         * Task was received by a worker (only used in events).
         */
        RECEIVED,
        /**
         * Task was started by a worker (:setting:task_track_started).
         */
        STARTED,
        /**
         * Task succeeded
         */
        SUCCESS,
        /**
         * Task failed
         */
        FAILURE,
        /**
         * Task was revoked.
         */
        REVOKED,
        /**
         * Task was rejected (only used in events).
         */
        REJECTED,
        /**
         * Task is waiting for retry.
         */
        RETRY,
        /**
         * Job Status IGNORED
         */
        IGNORED
    }

    public enum PurgeBlobStatus {
        /**
         * Successfully removed blob
         */
        SUCCESS,
        /**
         * Purge failed
         */
        FAILED
    }
}
