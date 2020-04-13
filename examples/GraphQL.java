import com.indico.IndicoClient;
import com.indico.IndicoConfig;
import com.indico.request.GraphQLRequest;
import org.json.JSONObject;
import java.io.IOException;

public class GraphQL {

    public static void main(String args[]) throws IOException {
        IndicoConfig config = new IndicoConfig.Builder()
                .host("dev.indico.io")
                .tokenPath("__TOKEN_PATH__")
                .build();

        try (IndicoClient client = new IndicoClient(config)) {
            GraphQLRequest request = indico.graphQLRequest();
            String query
                    = "query GetDatasets {\n"
                    + "    datasets {\n"
                    + "        id\n"
                    + "        name\n"
                    + "        status\n"
                    + "        rowCount\n"
                    + "        numModelGroups\n"
                    + "        modelGroups {\n"
                    + "            id\n"
                    + "        }\n"
                    + "    }\n"
                    + "}";

            JSONObject response = request.query(query).call();
            System.out.println(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
