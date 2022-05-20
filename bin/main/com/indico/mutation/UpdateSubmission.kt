package com.indico.mutation

import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.indico.IndicoClient
import com.indico.entity.Submission
import com.indico.exceptions.IndicoMutationException
import com.indico.graphql.UpdateSubmissionGraphQL
import com.indico.type.SubmissionStatus

class UpdateSubmission(client: IndicoClient) : Mutation<Submission?, UpdateSubmissionGraphQL.Result>() {
    private val client: IndicoClient
    private var submissionId = 0
    private var retrieved = false

    /**
     * Id of the submission to update
     * @param id submissionId
     * @return UpdateSubmission
     */
    fun submissionId(id: Int): UpdateSubmission {
        submissionId = id
        return this
    }

    /**
     * Mark the submission as having been retrieved
     * @param retrieved retrieved
     * @return UpdateSubmission
     */
    fun retrieved(retrieved: Boolean): UpdateSubmission {
        this.retrieved = retrieved
        return this
    }

    /**
     * Update properties of the Submission object
     * @return Submission
     */
    override fun execute(): Submission? {
        return try {
            val call = UpdateSubmissionGraphQL(UpdateSubmissionGraphQL.Variables(
                submissionId = this.submissionId,
                retrieved = OptionalInput.Defined(this.retrieved)
            ))
            val response = client.execute(call)
            handleErrors(response)
            val submission = response.data!!.updateSubmission!!
            Submission.Builder()
                .id(submission.id!!)
                .datasetId(submission.datasetId!!)
                .workflowId(submission.workflowId!!)
                .status(submission.status?.toString()?.let { SubmissionStatus.valueOf(it) })
                .inputFile(submission.inputFile!!)
                .inputFilename(submission.inputFilename!!)
                .resultFile(submission.resultFile!!)
                .retrieved(submission.retrieved!!)
                .build()
        } catch (ex: RuntimeException) {
            throw IndicoMutationException("Call to generate the submission result failed", ex)
        }
    }

    init {
        this.client = client
    }
}