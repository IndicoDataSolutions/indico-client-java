package com.indico.jobs;

import java.util.List;
import java.util.ArrayList;
import com.indico.Enums.JobStatus;

/**
 * Async Job information
 */
public class Job {
    public String id;

    /**
     * Retrieve job status
     * @return
     */
    public JobStatus status() {
        return JobStatus.SUCCESS;
    }

    /**
     * Retrieve results. Status must be success or an error will be thrown. 
     * @return
     */
    public String result() {
        return "https://app.indico.io/storage/12232-12321-32131-12312";
    }

    /**
     * If job status is FAILURE returns the list of errors encoutered
     */
    public List<String> errors() {
        return new ArrayList<String>();
    };
}
