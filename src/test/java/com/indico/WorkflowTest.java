package com.indico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.indico.jobs.*;
import com.indico.storage.PurgeBlobResult;
import com.indico.workflows.*;
import com.indico.Enums.JobStatus;
import com.indico.Enums.PurgeBlobStatus;

import java.io.File;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

import com.indico.IndicoClient;
import com.indico.IndicoConfig;

public class WorkflowTest {
    @Test
    @DisplayName("Simple example of workflow submission")
    public void test_simple_workflow_submission() throws java.lang.InterruptedException, java.io.IOException {

        // Initialize indico client and configuration. API Token available under your account page in the indico web app.
        // Host is your indico installation
        final IndicoClient client = new IndicoClient(new IndicoConfig.Builder()
                .apiToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MTEwNDksInVzZXJfaWQiOjU3NT...")
                .host("app.indico.io").build());

        // In order to submit a document to a workflow you need to create a workflow
        // submission object
        // In this case we provide a filepath which will be read into memory and sent to
        // to the endpoint
        // A com.indico.jobs.Job object is returned which can be used to monitor the
        // progress of processing the document
        // The workflow handles all operations for ETL of the document and running
        // appropriate models

        final WorkflowSubmission ws = client.workflowSubmission();
        final Job job = ws.workflow_id(0).filepath("/tmp/somefile").jobOptions(new JobOptions.Builder().priority(2).build()).execute();

        // The status of the job can be monitored using polling
        JobStatus status = job.status();
        while (status != JobStatus.SUCCESS || status != JobStatus.FAILURE) {
            Thread.sleep(1000);
            status = job.status();
        }

        // If errors occur, a list of error strings is available on the Job object
        if (status == JobStatus.FAILURE) {
            for (final String error : job.errors()) {
                System.out.println(error);
            }
        }

        // The following retrieves the job results. The job itself will have a URL for
        // the result file
        // You can then use our Blob interface to retrieve the result file
        final String url = job.result();
        final InputStream resultStream = client.retrieveBlob().url(url).getInputStream();

        final File outFile = new File("tmp/somefile_result.zip");
        java.nio.file.Files.copy(resultStream, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // The following purges the result file once you have successfully retrieved it.
        // This is a synchroneous operation
        // A PurgeBlobResult object will be returned with a status and if they occur a
        // list of errors
        final PurgeBlobResult purgeResult = client.purgeBlob().url(url).execute();
        if (purgeResult.status != PurgeBlobStatus.SUCCESS) {
            for (final String error : purgeResult.errors) {
                System.out.println(error);
            }
        }
       
    }
}