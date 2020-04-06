import com.indico.IndicoClient;
import com.indico.IndicoConfig;
import com.indico.entity.Model;
import com.indico.entity.ModelGroup;
import com.indico.query.ModelGroupQuery;
import com.indico.query.TrainingModelWithProgressQuery;
import java.io.IOException;

public class GetTrainingModelProgress {

    public static void main(String args[]) throws IOException {
        IndicoConfig config = new IndicoConfig.Builder()
                .host("dev.indico.io")
                .tokenPath("__TOKEN_PATH__")
                .build();

        try (IndicoClient client = new IndicoClient(config)) {
            ModelGroupQuery modelGroupQuery = client.modelGroupQuery();
            TrainingModelWithProgressQuery trainingModelWithProgress = client.trainingModelWithProgressQuery();
            ModelGroup modelGroup = modelGroupQuery.id(__MODEL_ID__).query();
            Model model = trainingModelWithProgress.id(__MODEL_ID__).query();
            System.out.println(modelGroup.name);
            System.out.println("training status : " + model.status);
            System.out.println("percent complete : " + model.trainingProgress.percentComplete);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

