
import com.indico.IndicoClient;
import com.indico.IndicoConfig;
import org.json.JSONObject;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import software.amazon.awssdk.utils.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SQSExample {
    private static final String QUEUE_NAME = "CHANGE_ME_QUEUE";
    private static final String PDF_LOCATION = "CHANGE_ME_PDF";
    private static final String HOST = "app.indico.io";
    private static final String TOKEN = "CHANGE_ME_TOKEN_PATH";
    private static final int WORKFLOW_ID = 0;
    private static final Region REGION = Region.US_WEST_2;

    public static void main(String[] args) throws InterruptedException, IOException {
        //todo: configure your access accordingly before running
        //see: https://docs.aws.amazon.com/sdk-for-java/latest/developerx-guide/credentials.html
        SqsClient sqsClient = SqsClient.builder()
                .region(REGION)
                .build();
        String queueUrl = QUEUE_NAME;

        //configure an indico client
        String host =  HOST;
        String tokenPath = TOKEN;
        IndicoConfig config = new IndicoConfig.Builder().host(host)
                .protocol("https")
                .tokenPath(tokenPath)
                .connectionReadTimeout(60)
                .connectionWriteTimeout(60)
                .maxRetries(5)
                .connectTimeout(0)
                .build();

        IndicoClient indicoClient = new IndicoClient(config);
        //submit item(s) for processing
        submit_workflow(indicoClient);

        //generate a request to receive msg from the queue to bootstrap the process.
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                //30 seconds -- see: https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-visibility-timeout.html
                .visibilityTimeout(30)
                .build();

        //request the messages
        var messages = sqsClient.receiveMessage(request);
        while(true) {
            // iterate the messages from SQS
            for (Message m : messages.messages()) {
                //fetch receipt handle and body
                String rh = m.receiptHandle();
                //handle the fact that certain SQS queues
                //may escpae JSON even with raw value setting
                if(m.body().startsWith("\"")) {
                    StringBuilder sb = new StringBuilder(m.body());
                    sb.deleteCharAt(0);
                    while (sb.indexOf("\\") > -1) {
                        int indx = sb.indexOf("\\");
                        sb.deleteCharAt(indx);
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    bod = sb.toString();
                }

                JSONObject body = new JSONObject(bod);
                var status = body.getString("status");
                var url = body.getString("result_url");
                var id = body.getInt("submission_id");

                System.out.println(body);
                if(status.equalsIgnoreCase("COMPLETED")){
                    System.out.println("Completed: " + id);
                    //process a completed message
                    process_result(indicoClient, id, url);
                } else if(status.equalsIgnoreCase("FAILED")){
                    //handle failures as seen fit.
                    System.out.print("Submission failed: " + id);
                }
                //remove message when processed..
                var delmsg = DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(rh)
                        .build();
                sqsClient.deleteMessage(delmsg);
            }
            //loop and wait for more messages
            Thread.sleep(3000);
            messages = sqsClient.receiveMessage(request);
        }

    }

    public static void process_result(IndicoClient indicoClient, int id, String url) throws IOException {
        System.out.println("get blob....");
        var ret_storage_obj = indicoClient.retrieveBlob();
        ret_storage_obj.url(url);
        ret_storage_obj.execute();
        System.out.println("update sub....");
        var update_sub = indicoClient.updateSubmission();
        update_sub.submissionId(id);
        update_sub.retrieved(true);
        update_sub.execute();
    }

    public static void submit_workflow(IndicoClient indicoClient){
        //todo: change this to your workflow id
        int workflow_id = WORKFLOW_ID;
        String pdf = PDF_LOCATION;
        List<String> files = new ArrayList<String>();
        files.add(pdf);
        var submission = indicoClient.workflowSubmission();
        submission.files(files);
        submission.workflowId(workflow_id);
        submission.execute();
    }

}
