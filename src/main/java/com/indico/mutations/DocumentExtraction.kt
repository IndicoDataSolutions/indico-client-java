package com.indico.mutations

import com.indico.IndicoKtorClient
import com.indico.Mutation
import com.indico.exceptions.IndicoMutationException
import com.indico.graphql.DocumentExtractionGraphQL
import com.indico.graphql.inputs.FileInput
import com.indico.query.Job
import org.json.JSONObject
import java.util.ArrayList
import kotlin.RuntimeException

class DocumentExtraction(private val indicoClient: IndicoKtorClient) : Mutation<List<Job?>?, DocumentExtractionGraphQL.Result>() {
    private var files: List<String>? = null
    private var jsonConfig: JSONObject? = null

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
        val files: MutableList<FileInput> = ArrayList<FileInput>()
        try {
            files.addAll(processFiles(this.files!!, this.indicoClient))
        } catch (ex: RuntimeException) {
            throw  IndicoMutationException("Failed to upload files for extraction", ex)
        }
        return try {

            val call = DocumentExtractionGraphQL(
                variables = DocumentExtractionGraphQL.Variables(
                    files = files,
                    jsonConfig = this.jsonConfig.toString()
                )
            );

            val response = this.indicoClient.execute(call)

            handleErrors(response)
            val jobIds = response.data!!.documentExtraction!!.jobIds
            val jobs: MutableList<Job> = ArrayList<Job>()
            for (id in jobIds!!) {
                val job = Job(indicoClient, id = id!!, errors = null)
                jobs.add(job)
            }
            jobs
        } catch (ex: RuntimeException) {
            throw IndicoMutationException("Call to DocumentExtraction failed", ex)
        }
    }

}