package com.indico.mutation

import com.indico.IndicoKtorClient
import com.indico.Mutation
import com.indico.exceptions.IndicoMutationException
import com.indico.graphql.DocumentExtractionGraphQL
import com.indico.graphql.inputs.FileInput
import com.indico.query.Job
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder
import java.util.ArrayList

class DocumentExtraction(indicoClient: IndicoKtorClient) : Mutation<List<Job?>?> {
    private var files: List<String>? = null
    private var jsonConfig: JSONObject? = null
    private val indicoClient: IndicoKtorClient

    /**
     * Files to extract
     *
     * @param files File paths
     * @return DocumentExtraction
     */
    fun files(files: List<String>?): DocumentExtraction {
        this.files = files
        jsonConfig = JSONObject()
        jsonConfig!!.put("preset_config", "simple")
        return this
    }

    /**
     * JSON configuration for extraction
     *
     * @param jsonConfig JSON config
     * @return DocumentExtraction
     */
    fun jsonConfig(jsonConfig: JSONObject?): DocumentExtraction {
        this.jsonConfig = jsonConfig
        return this
    }

    /**
     * Executes request and returns Jobs
     *
     * @return Job List
     */
    override fun execute(): List<Job> {
        val fileMetadata: JSONArray
        val files: MutableList<FileInput> = ArrayList<FileInput>()
        try {
            fileMetadata = upload(this.files)
            for (f in fileMetadata) {
                val uploadMeta = f as JSONObject
                val meta = JSONObject()
                meta.put("name", uploadMeta.getString("name"))
                meta.put("path", uploadMeta.getString("path"))
                meta.put("upload_type", uploadMeta.getString("upload_type"))
                val input = FileInput(filename = f.getString("name"), filemeta = meta)
                files.add(input)
            }
        }
        return try {

            val call = DocumentExtractionGraphQL(variables = DocumentExtractionGraphQL.Variables(files = files,
                jsonConfig = this.jsonConfig.toString()));


            val response = this.indicoClient.execute(call)

            if (response.errors != null && response.errors!!.isNotEmpty()) {
                val errors = StringBuilder()
                for (err in response.errors!!.asIterable()) {
                    errors.append(err.toString() + "\n")
                }
                val msg = errors.toString()
                throw IndicoMutationException("Failed to extract documents due to following error: \n$msg")
            }
            val jobIds = response.data!!.documentExtraction!!.jobIds
            val jobs: MutableList<Job> = ArrayList<Job>()
            for (id in jobIds!!) {
                val job = Job(indicoClient, id =id!!, errors =null)
                jobs.add(job)
            }
            jobs
        }  catch (ex: InterruptedException) {
            throw IndicoMutationException("Call to DocumentExtraction failed", ex)
        }
    }

    private fun upload(filePaths: List<String>?): JSONArray {
        val uploadRequest = UploadFile(indicoClient)
        return uploadRequest.filePaths(filePaths).call()
    }

    init {
        this.indicoClient = indicoClient
    }
}