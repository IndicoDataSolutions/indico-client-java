import com.indico.IndicoClient;
import com.indico.IndicoConfig;
import com.indico.entity.Workflow;
import com.indico.jobs.Job;
import com.indico.mutation.WorkflowJob;
import com.indico.storage.Blob;
import com.indico.storage.RetrieveBlob;
import com.indico.type.JobStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/***
 * Used for one-off jobs. Most use cases want to follow the pattern in Submission.java instead.
 */
public class Workflow {

    public static void main(String args[]) throws IOException {
        IndicoConfig config = new IndicoConfig.Builder()
                .host("app.indico.io")
                .tokenPath("__TOKEN_PATH__")
                .build();

        // Use your dataset's id to call it's associated workflow
        int datasetId = 6826;

        try (IndicoClient client = new IndicoClient(config)) {
            List<Integer> datasetIds = new ArrayList<>();
            datasetIds.add(datasetId);

            // Return a list of workflows for this dataset id or an empty list if there are none
            List<Workflow> workflows = client.listWorkflows().datasetIds(datasetIds).query();
            if(workflows.size() > 0) {
                WorkflowJob workflowJob = client.workflowJob();
                ArrayList<String> files = new ArrayList<>();
                files.add("./path_to_file.pdf");
                int workflowId = workflows.get(0).id;
                List<Job> jobs = workflowJob.files(files).workflowId(workflowId).execute();
                Job job = jobs.get(0);

                while (job.status() == JobStatus.PENDING) {
                    Thread.sleep(1000);
                }

                JSONObject obj = job.result();
                String url = obj.getString("url");
                RetrieveBlob retrieveBlob = client.retrieveBlob();
                Blob blob = retrieveBlob.url(url).execute();
                System.out.println(blob.asString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
