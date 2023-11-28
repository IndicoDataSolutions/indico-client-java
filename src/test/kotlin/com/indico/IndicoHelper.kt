package com.indico

class IndicoHelper {

    fun getIndico(): IndicoClient {
        val indicoConfig = IndicoConfig.Builder()
            .host("dev-ci.us-east-2.indico-dev.indico.io")
            .protocol("https")
            .maxConnections(Integer.MAX_VALUE)
            .tokenPath("./")
            .build()
        return IndicoKtorClient(indicoConfig)
    }
    fun getWorkflowId(): Int {
       return 1655
   }

    fun getEnvUrl(): String{
        return "dev-ci.us-east-2.indico-dev.indico.io"
    }

    fun getDatasetLocation(): String {
        return "./src/test/data/"
    }
}