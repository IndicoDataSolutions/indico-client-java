package examples;

import com.indico.IndicoClient;
import com.indico.IndicoConfig;
import com.indico.IndicoKtorClient;
import com.indico.exceptions.IndicoBaseException;
import com.indico.mutation.DocumentExtraction;
import com.indico.query.Job;
import com.indico.storage.Blob;
import com.indico.storage.RetrieveBlob;
import com.indico.type.JobStatus;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class SingleDocExtraction {

    public static void main(String args[]) throws IOException {
        IndicoConfig config = new IndicoConfig.Builder()
                .host("dev.indico.io")
                .tokenPath("__TOKEN_PATH__")
                .build();

        try (IndicoClient client = new IndicoKtorClient(config)) {
            DocumentExtraction extraction = client.documentExtraction();
            ArrayList<String> files = new ArrayList<>();
            files.add("__PDF_PATH__");
            JSONObject json = new JSONObject();
            json.put("preset_config", "simple");
            extraction.files(files).jsonConfig(json);
            List<Job> jobs = extraction.execute();
            Job job = jobs.get(0);
            while (job.status() == JobStatus.PENDING) {
                Thread.sleep(1000);
                jobs = extraction.execute();
                job = jobs.get(0);
            }
            JSONObject obj = job.result();
            String url = obj.getString("url");
            RetrieveBlob retrieveBlob = client.retrieveBlob();
            Blob blob = retrieveBlob.url(url).execute();
            //call close on blob to dispose when done with object.
            blob.close();
            System.out.println(blob.asString());
        } catch (IndicoBaseException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}