package com.indico.type

enum class JobStatus {
    /**
     * Job has failed
     */
    FAILURE,

    /**
     * Ignored.
     */
    IGNORED,

    /**
     * Job status not yet known.
     */
    PENDING,

    /**
     * Event only - Worker received the job.
     */
    RECEIVED,

    /***
     * Events only - Job was rejected.
     */
    REJECTED,

    /**
     * Job is being retried.
     */
    RETRY,

    /**
     * Job was revoked.
     */
    REVOKED,

    /**
     * Worker has started the task.
     */
    STARTED,

    /**
     * Job was successful.
     */
    SUCCESS,
    TRAILED
}