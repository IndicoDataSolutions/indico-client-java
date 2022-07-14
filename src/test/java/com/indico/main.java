package com.indico;

import com.indico.entity.ModelGroup;
import com.indico.entity.Submission;

import com.indico.entity.SubmissionRetries;
import com.indico.mutation.DocumentExtraction;
import com.indico.mutation.RetrySubmission;
import com.indico.mutation.UpdateSubmission;
import com.indico.mutation.WorkflowSubmission;
import com.indico.query.GetSubmission;
import com.indico.query.Job;
import com.indico.query.ListSubmissions;
import com.indico.query.ModelGroupQuery;
import com.indico.storage.Blob;
import com.indico.storage.RetrieveBlob;
import com.indico.type.JobStatus;
import com.indico.type.SubmissionResultVersion;
import com.indico.type.SubmissionStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionException;


public class main {

    public static IndicoKtorClient client;
    private static String token_path = "./indico_api_token.txt";

    public static void main(String args[]) throws Exception {

        Integer workflow_id = 16575;
        /**
         * Set up client
         */
        String host = "dev.indico.io";
        IndicoConfig config = new IndicoConfig.Builder().host(host)
                .protocol("https")
                .tokenPath(token_path)
                .connectionReadTimeout(60)
                .connectionWriteTimeout(60)
                .maxRetries(5)
                .connectTimeout(0)
                .build();
        client = new IndicoKtorClient(config);

        /**
         * Submit to workflow
         */
        WorkflowSubmission submitFile = client.workflowSubmission();
        List<String> files = new ArrayList<String>();
        files.add("/home/mcahill/indico/indico-client-java2/src/test/data/pdf1.pdf");
        List<Integer> ids = submitFile.files(files)
                .workflowId(16575)
                .execute();

        for(Integer id : ids){
            System.out.println(id);
        }
        GetSubmission submission = client.getSubmission();
        submission.submissionId(ids.get(0));
        Submission result = submission.query();
        while(result.status != SubmissionStatus.COMPLETE && result.status != SubmissionStatus.FAILED){
            result = submission.query();
            System.out.println(result.status);
        }
        System.out.println("Submission result:" + result.status);

        RetrySubmission retry = client.retrySubmission();

        retry.ids(ids);
        List<SubmissionRetries> retries = retry.execute();


        if(result.status == SubmissionStatus.COMPLETE) {
            System.out.println("Processing....");
            process_result(result, client);
        }



        /**
         * Submit to document extraction
         */
        DocumentExtraction docEx = client.documentExtraction();
        docEx.files(files);
        List<Job> jobs = docEx.execute();
        Job job = jobs.get(0);
        while(job.status() == JobStatus.PENDING)
        {
            job.status();
        }
        System.out.println("Doc extraction: " + job.status());
        System.out.println(job.resultAsString());

        System.exit(0);

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
            System.out.println("Failed to update sub...");
            ex.printStackTrace();
        }finally{
            if(blob != null){
                blob.close();
            }
        }

    }
}


/*
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
    }

 */