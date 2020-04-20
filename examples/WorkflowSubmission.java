import com.indico.IndicoClient;
import com.indico.IndicoConfig;
import com.indico.entity.Workflow;
import com.indico.jobs.Job;
import com.indico.mutation.WorkflowSubmission;
import com.indico.query.ListWorkflowsForDatasetQuery;
import com.indico.storage.Blob;
import com.indico.storage.RetrieveBlob;
import com.indico.type.JobStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONObject;

public class WorkflowSubmission {

    public static void main(String args[]) throws IOException {
        IndicoConfig config = new IndicoConfig.Builder()
                .host("dev.indico.io")
                .tokenPath("__TOKEN_PATH__")
                .build();

        try (IndicoClient client = new IndicoClient(config)) {
            ListWorkflowsForDatasetQuery listWorkflows = client.listWorkflowsForDatasetQuery();
            List<Workflow> workflows = listWorkflows.datasetId(__DATASET_ID__).query();
            Workflow wf = Collections.max(workflows, Comparator.comparingInt(w -> w.id));

            WorkflowSubmission workflowSubmission = client.workflowSubmission();
            ArrayList<String> files = new ArrayList<>();
            files.add(__PDF_PATH__);
            Job job = workflowSubmission.files(files).workflowId(wf.id).execute();
            while (job.status() == JobStatus.PENDING) {
                System.out.println(job.status() + " " + job.id);
                Thread.sleep(1000);
            }
            JSONObject obj = job.result();
            String url = obj.getString("url");
            RetrieveBlob retrieveBlob = client.retrieveBlob();
            Blob blob = retrieveBlob.url(url).execute();
            System.out.println(blob.asString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
