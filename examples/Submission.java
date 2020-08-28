import com.indico.IndicoClient;
import com.indico.IndicoConfig;
import com.indico.entity.Submission;
import com.indico.jobs.Job;
import com.indico.mutation.WorkflowSubmission;
import com.indico.storage.Blob;
import com.indico.storage.RetrieveBlob;
import com.indico.type.JobStatus;
import com.indico.type.SubmissionFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import org.json.JSONObject;

public class Submission {

    public static void main(String args[]) throws IOException {
        IndicoConfig config = new IndicoConfig.Builder()
                .host("app.indico.io")
                .tokenPath("__TOKEN_PATH__")
                .build();
        
        int workflowId = 5;
        
        try (IndicoClient client = new IndicoClient(config)) {
            /*
            * Create a new submission 
            * Generate a submission result as soon as the submission is done processing
            * Then mark the submission has having been retrieved
            */
            WorkflowSubmission workflowSubmission = client.workflowSubmission();
            ArrayList<String> files = new ArrayList<>();
            files.add("./path_to_file.pdf");
            List<Integer> submissionIds = workflowSubmission.files(files).workflowId(workflowId).execute();
            int submissionId = submissionIds[0];
            Job job = client.submissionResult().submission(submissionId).execute();
            
            while (job.status() == JobStatus.PENDING) {
                Thread.sleep(1000);
            }

            JSONObject obj = job.result();
            String url = obj.getString("url");
            RetrieveBlob retrieveBlob = client.retrieveBlob();
            Blob blob = retrieveBlob.url(url).execute();
            System.out.println(blob.asString());
            client.updateSubmission().submissionId(submissionId).retrieved(true).execute();
            
            /*
            * List all submissions that are COMPLETE or FAILED
            * Generate submission results for these
            * Delay gathering the results until required
            */
            List<SubmissionFilter> filters = new ArrayList<>();
            filters.add(SubmissionFilter.builder().status(SubmissionStatus.COMPLETE).build());
            filters.add(SubmissionFilter.builder().status(SubmissionStatus.FAILED).build());
            SubmissionFilter subFilter = SubmissionFilter.builder().oR(filters).build();
            List<Submission> submissions = client.listSubmissions().filters(subFilter).query();
            Hashtable<Submission, Job> resultFiles = new Hashtable<>();

            for(Submission s : submissions) {
                Job j = client.generateSubmissionResult().submission(s).execute();
                resultFiles.put(submission, j);
            }
            
            // Do other fun things

            Set<Submission> keySet = resultFiles.keySet();
            for(Submission submission : keySet) {
                Job job = resultFiles.get(submission);

                while (job.status() == JobStatus.PENDING) {
                    Thread.sleep(1000);
                }

                JSONObject obj = job.result();
                String url = obj.getString("url");
                RetrieveBlob retrieveBlob = client.retrieveBlob();
                Blob blob = retrieveBlob.url(url).execute();
                System.out.println("Submission " + submission.id + " has result " + blob.asString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
