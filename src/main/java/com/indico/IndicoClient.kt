package com.indico

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.indico.mutations.*
import com.indico.query.*

interface IndicoClient {

    fun close()
    fun <T : Any> execute(request: GraphQLClientRequest<T>) : GraphQLClientResponse<T>
    suspend fun <T : Any> executeAsync(request: GraphQLClientRequest<T>) : GraphQLClientResponse<T>
    fun listSubmissions(): ListSubmissions?
    fun workflowSubmission(): WorkflowSubmission?
    fun documentExtraction(): DocumentExtraction?
    fun generateSubmissionResult(): GenerateSubmissionResult?
    fun modelGroupLoad(): ModelGroupLoad?
    fun modelGroupPredict(): ModelGroupPredict?
    fun submissionResult(): SubmissionResult?
    fun updateSubmission(): UpdateSubmission?
    fun getSubmission(): GetSubmission?
    fun modelGroupQuery(): ModelGroupQuery?
    fun trainingModelWithProgress(): TrainingModelWithProgressQuery?

}