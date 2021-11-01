package com.indico.query

import com.indico.IndicoClient
import com.indico.JSON
import com.indico.exceptions.IndicoQueryException
import com.indico.graphql.JobResultGraphQL
import com.indico.type.JobStatus
import com.indico.graphql.jobresultgraphql.Job
import org.json.JSONArray
import org.json.JSONObject
import java.lang.RuntimeException
import java.util.ArrayList
import com.indico.graphql.enums.JobStatus as GraphQlJobStatus

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
        return result.status.toString().let {JobStatus.valueOf(it) }
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
            val status: GraphQlJobStatus? = job?.status
       
            job!!
        } catch (ex: RuntimeException) {
            throw IndicoQueryException("Call for the job result failed", ex)
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