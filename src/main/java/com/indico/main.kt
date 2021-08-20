package com.indico

import com.indico.graphql.*
import com.indico.IndicoKtorClient
import com.indico.IndicoConfig
import com.indico.main
import com.indico.query.ListSubmissions
import kotlinx.coroutines.runBlocking


fun main() {
        val client: IndicoKtorClient
        val token_path = "./"
        val workflow_id = 16575
        val host = "dev.indico.io"
        val config = IndicoConfig.Builder().host(host)
            .protocol("https")
            .tokenPath(token_path)
            .connectionReadTimeout(60)
            .connectionWriteTimeout(60)
            .maxRetries(5)
            .connectTimeout(0)
            .build()
        client = IndicoKtorClient(config)
        val query = client.listSubmissions()
        val result = query!!.query()
        for (sub in result) {
            println(sub.id)
        }
    } /*
    public static void main(String args[]) throws Exception {
        Integer workflow_id = 16575;
        String host = "dev.indico.io";

        IndicoConfig config = new IndicoConfig.Builder().host(host)
                .protocol("https")
                .tokenPath(token_path)
                .connectionReadTimeout(60)
                .connectionWriteTimeout(60)
                .maxRetries(5)
                .connectTimeout(0)
                .build();
            client = new IndicoClient(config);

        ArrayList<String> filepaths = new ArrayList<String>();
        filepaths.add("./files/ACORD80Filledout.pdf");
        workflowSubmission(orkflow_id);


    }

    public static void workflowSubmission(int workflowId) throws Exception {

        WorkflowSubmission workflowJob = client.workflowSubmission();
        Path path = Paths.get("./img.png");
        byte[] data = Files.readAllBytes(path);
        Map<String, byte[]> streamMap = new HashMap<String, byte[]>();
        streamMap.put("image.png", data);

        List<Integer> jobs = workflowJob.byteStreams(streamMap).workflowId(workflowId).execute();
        GetSubmission submission = client.getSubmission();
        submission.submissionId(jobs.get(0));
        Submission result = submission.query();
        System.out.println(jobs.get(0));
        while(result.status != SubmissionStatus.COMPLETE){
            result = submission.query();
            System.out.println(result.status);
        }
        System.out.println("Processing....");
        process_result(result, client);
    }

    public static void process_result(Submission submission, IndicoClient indicoClient) throws Exception {
        Blob blob = null;
        try {
            String url = "https://dev.indico.io/"+submission.resultFile;
            RetrieveBlob ret_storage_obj = indicoClient.retrieveBlob();
            ret_storage_obj.url(url);
            blob = ret_storage_obj.execute();
            blob.close();
            UpdateSubmission update_sub = indicoClient.updateSubmission();
            update_sub.submissionId(submission.id);
            update_sub.retrieved(true);
            update_sub.execute();

        }catch (CompletionException ex){
            if(blob != null){
                blob.close();
            }
            System.out.println("Failed to update sub...");
            ex.printStackTrace();
        }finally{
            if(blob != null){
                blob.close();
            }
        }

    }*/
