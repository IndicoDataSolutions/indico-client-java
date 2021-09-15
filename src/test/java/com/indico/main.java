package com.indico;

import com.indico.entity.Submission;

import com.indico.mutations.DocumentExtraction;
import com.indico.mutations.WorkflowSubmission;
import com.indico.query.Job;
import com.indico.query.ListSubmissions;
import com.indico.query.ModelGroupQuery;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class main {

    public static IndicoKtorClient client;
    private static String token_path = "./";

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
        client = new IndicoKtorClient(config);

        ListSubmissions query = client.listSubmissions();

        List<Submission> result = query.query();
        
        WorkflowSubmission submitFile = client.workflowSubmission();
        List<String> files = new ArrayList<String>();
        files.add("/home/mcahill/indico/indico-client-java2/src/test/data/pdf0.pdf");
        List<Integer> ids = submitFile.files(files)
                .workflowId(16575)
                .execute();

        submitFile = client.workflowSubmission();
        ids = submitFile.files(files)
                .workflowId(16575)
                .execute();
        for(Integer id : ids){
            System.out.println(id);
        }

        DocumentExtraction docEx = client.documentExtraction();
        docEx.files(files);
        List<Job> jobs = docEx.execute();

        ModelGroupQuery mgq = client.modelGroupQuery();
        mgq.id()
        System.exit(0);

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