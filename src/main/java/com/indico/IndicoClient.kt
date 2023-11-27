package com.indico

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.fasterxml.jackson.databind.JsonNode
import com.indico.mutation.*
import com.indico.query.*
import com.indico.request.GraphQLRequest
import com.indico.storage.RetrieveBlob
import com.indico.storage.UploadFile

/**
 * Indico Client for communicating with the platform's GraphQL API.
 */
interface IndicoClient: AutoCloseable {

    /**
     * Close and dispose of connections.
     */
    override fun close()

    /**
     * Execute any request which inherits from GraphQLClientRequest synchronously.
     */
    fun <T : Any> execute(request: GraphQLClientRequest<T>) : GraphQLClientResponse<T>

    /**
     * Execute any request which inherits from GraphQLClientRequest asynchronously.
     */
    suspend fun <T : Any> executeAsync(request: GraphQLClientRequest<T>) : GraphQLClientResponse<T>

    /**
     * Query for submissions and recieve a list.
     */
    fun listSubmissions(): ListSubmissions?

    /**
     * Execute a mutation which submits to a workflow and generates a submission.
     */
    fun workflowSubmission(): WorkflowSubmission?

    /**
     * Execute a mutation to extract information from a document.
     */
    @Deprecated("Not supported")
    fun documentExtraction(): DocumentExtraction?

    /**
     * Generate submission results post-submission and post-review.
     */
    fun generateSubmissionResult(): GenerateSubmissionResult?

    /**
     * Load a model group given a ModelGroup or an integer id.
     */
    fun modelGroupLoad(): ModelGroupLoad?

    /**
     * Retrieve model group predictions.
     */
    fun modelGroupPredict(): ModelGroupPredict?

    /**
     * Retrieve submission result, especially after running GenerateSubmissionResult
     */
    fun submissionResult(): SubmissionResult?

    /**
     * Execute a mutation to update the status of a submission.
     */
    fun updateSubmission(): UpdateSubmission?

    /**
     * Retrieve a particular submission.
     */
    fun getSubmission(): GetSubmission?

    /**
     * Retrieve a particular model group.
     */
    fun modelGroupQuery(): ModelGroupQuery?

    /**
     * Retrieve information about the training progress of a model.
     */
    fun trainingModelWithProgressQuery(): TrainingModelWithProgressQuery?

    /**
     * Download a blob. Must call close() on the retrieved blob to dispose of the object after.
     */
    fun retrieveBlob(): RetrieveBlob?

    /**
     * Upload a particular file.
     */
    fun uploadFile(): UploadFile?

    /**
     * Retry a submission which has failed or otherwise is not completed.
     */
    fun retrySubmission(): RetrySubmission?

    /**
     * Make raw GraphQL query
     */
    fun rawGraphQLQuery(query: String, operationName: String, variables: JsonNode): GraphQLRequest?
}