package com.indico;

import com.indico.entity.Submission;

import com.indico.query.ListSubmissions;
import java.util.List;


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

        for (Submission sub : result) {

            System.out.println(sub.id);

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