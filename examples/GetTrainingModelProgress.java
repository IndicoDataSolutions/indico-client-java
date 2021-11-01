package examples;

import com.indico.IndicoClient;
import com.indico.IndicoConfig;
import com.indico.IndicoKtorClient;
import com.indico.entity.Model;
import com.indico.entity.ModelGroup;
import com.indico.exceptions.IndicoBaseException;
import com.indico.query.ModelGroupQuery;
import com.indico.query.TrainingModelWithProgressQuery;
import java.io.IOException;

public class GetTrainingModelProgress {

    public static void main(String args[]) throws IOException {
        IndicoConfig config = new IndicoConfig.Builder()
                .host("dev.indico.io")
                .tokenPath("__TOKEN_PATH__")
                .build();

        int modelGroupId = 1;

        try (IndicoClient client = new IndicoKtorClient(config)) {
            ModelGroupQuery modelGroupQuery = client.modelGroupQuery();
            TrainingModelWithProgressQuery trainingModelWithProgress = client.trainingModelWithProgressQuery();
            ModelGroup modelGroup = modelGroupQuery.id(modelGroupId).query();
            Model model = trainingModelWithProgress.id(modelGroupId).query();
            System.out.println(modelGroup.name);
            System.out.println("training status : " + model.status);
            System.out.println("percent complete : " + model.trainingProgress.percentComplete);

        } catch (IndicoBaseException e) {
            e.printStackTrace();
        }
    }
}

