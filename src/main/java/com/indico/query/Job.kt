package com.indico.query

import com.indico.IndicoClient
import com.indico.IndicoKtorClient
import com.indico.JSON
import com.indico.exceptions.IndicoQueryException
import com.indico.graphql.JobResultGraphQL
import com.indico.graphql.enums.JobStatus
import com.indico.graphql.jobresultgraphql.Job
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CompletionException
import java.lang.RuntimeException
import java.util.concurrent.ExecutionException
import java.lang.InterruptedException
import java.util.ArrayList

/**
 *  Job information
 */
class Job(private val indicoClient: IndicoClient, private val errors: List<String>?, val id: String) {

    /**
     * Retrieve job status
     *
     * @return JobStatus
     */
    fun status(): JobStatus {
       val result = fetchResult()
        return result.status!!
    }

    fun resultAsString(): String {
        return fetchResult().result.toString()
    }

    /**
     * Retrieve result. Status must be success or an error will be thrown.
     *
     * @return JSONObject
     */
    fun result(): JSONObject {
        val result = fetchResult().result
        return JSON(result).asJSONObject()
    }

    /**
     * Retrieve results. Status must be success or an error will be thrown.
     *
     * @return JSONArray
     */
    fun results(): JSONArray {
        val result = fetchResult().result
        return JSON(result).asJSONArray()
    }

    /**
     * Retrieve results as String
     *
     * @return Result String
     */
    private fun fetchResult(): Job {
        return try {
            val jobsQuery =  JobResultGraphQL(JobResultGraphQL.Variables(this.id))
            val jobResult = this.indicoClient.execute(jobsQuery)
            if(jobResult.errors != null && jobResult.errors!!.isNotEmpty()){
                throw IndicoQueryException(jobResult.errors!!.joinToString(separator = ","))
            }
            val job = jobResult.data!!.job
            val status: JobStatus? = job?.status
            if (status !== JobStatus.SUCCESS) {
                throw RuntimeException("Job finished with status : " + status?.toString())
            }

            job
        } catch (ex: CompletionException) {
            throw RuntimeException("Call for the job result failed", ex)
        } catch (ex: ExecutionException) {
            throw RuntimeException("Call for the job result failed", ex)
        } catch (ex: InterruptedException) {
            throw RuntimeException("Call for the job result failed", ex)
        }
    }


    /**
     * If job status is FAILURE returns the list of errors encoutered
     *
     * @return List of Errors
     */
    fun errors(): List<String> {
        return ArrayList()
    }
}