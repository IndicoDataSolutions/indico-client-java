package examples;

import com.indico.IndicoClient;
import com.indico.IndicoConfig;
import com.indico.IndicoKtorClient;
import com.indico.mutation.WorkflowSubmission;
import com.indico.query.Job;
import com.indico.storage.Blob;
import com.indico.storage.RetrieveBlob;
import com.indico.type.JobStatus;
import com.indico.type.SubmissionFilter;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.indico.type.SubmissionStatus;
import org.apache.commons.io.FileUtils;
import com.indico.entity.Submission;
import org.json.JSONObject;

public class SubmissionExample {

    public static void main(String args[]) throws IOException {
        IndicoConfig config = new IndicoConfig.Builder()
                .host("app.indico.io")
                .tokenPath("__TOKEN_PATH__")
                .build();

        int workflowId = 5;

        try (IndicoClient client = new IndicoKtorClient(config)) {

            /*
             * Create a new submission using one of two methods, file path or bytestream.
             * Generate a submission result as soon as the submission is done processing
             * Then mark the submission has having been retrieved
             */

            WorkflowSubmission workflowSubmission = client.workflowSubmission();

            //Method 1: Add the submission file as a path to a file.
            ArrayList<String> files = new ArrayList<>();
            files.add("./path_to_file.pdf");
            List<Integer> submissionIds = workflowSubmission.files(files).workflowId(workflowId).execute();
            int submissionId = submissionIds.get(0);

            /**
             * This call unifies the results from review with the submission result
             * and generates the final file.
             */
            Job job = client.submissionResult().submission(submissionId).execute();

            /**
             * When using either method, it is important to note that you must
             * re-execute the query to update the result status.
             */
            while (job.status() == JobStatus.PENDING) {
                Thread.sleep(1000);
                job = client.submissionResult().submission(submissionId).execute();
                System.out.println("Job Status: " + job.status());
            }

            JSONObject obj = job.result();
            String url = obj.getString("url");
            RetrieveBlob retrieveBlob = client.retrieveBlob();
            Blob blob = retrieveBlob.url(url).execute();
            /**
             * It is important to close blob storage objects to release the
             * connections that may remain from reading the blob.
             */
            blob.close();
            System.out.println(blob.asString());
            client.updateSubmission().submissionId(submissionId).retrieved(true).execute();

            //Method 2: Add the file(s) as byte streams.
            WorkflowSubmission byteWorkflowSubmission = client.workflowSubmission();
            String fileName = "./workflow-sample.pdf";
            byte[] bytes = FileUtils.readFileToByteArray(new File(fileName));
            Map<String, byte[]> maps = new HashMap<String,byte[]>();
            maps.put(fileName,bytes);
            submissionIds = byteWorkflowSubmission.byteStreams(maps).workflowId(workflowId).execute();
            int streamSubmissionId = submissionIds.get(0);
            /**
             * Essentially identical to the first method.
             */
            Job streamJob = client.submissionResult().submission(streamSubmissionId).execute();

            while (streamJob.status() == JobStatus.PENDING) {
                Thread.sleep(1000);
                streamJob = client.submissionResult().submission(submissionId).execute();
                System.out.println("Job Status: " + streamJob.status());
            }

            /*
             * List all submissions that are COMPLETE or FAILED
             * Generate submission results for these
             * Delay gathering the results until required
             */
            List<SubmissionFilter> filters = new ArrayList<>();
            filters.add(new SubmissionFilter.Builder().status(SubmissionStatus.COMPLETE).build());
            filters.add(new SubmissionFilter.Builder().status(SubmissionStatus.FAILED).build());
            SubmissionFilter subFilter = new SubmissionFilter.Builder().ors(filters).build();
            List<Submission> submissions = client.listSubmissions().filters(subFilter).query();
            Hashtable<Submission, Job> resultFiles = new Hashtable<>();

            for(Submission s : submissions) {
                Job j = client.generateSubmissionResult().submission(s).execute();
                resultFiles.put(s, j);
            }

            // Do other fun things

            Set<Submission> keySet = resultFiles.keySet();
            for(Submission s : keySet) {
                job = resultFiles.get(s);

                while (job.status() == JobStatus.PENDING) {
                    Thread.sleep(1000);
                }
                obj = job.result();
                url = obj.getString("url");
                retrieveBlob = client.retrieveBlob();
                blob = retrieveBlob.url(url).execute();
                System.out.println("Submission " + s.id + " has result " + blob.asString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
