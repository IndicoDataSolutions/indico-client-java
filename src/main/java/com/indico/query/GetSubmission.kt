package com.indico.query


import com.indico.IndicoClient
import com.indico.entity.Submission
import com.indico.exceptions.IndicoQueryException
import com.indico.graphql.GetSubmissionGraphQL
import com.indico.graphql.createsubmissionresultsgraphql.SubmissionResults
import com.indico.type.SubmissionStatus

class GetSubmission(private val client: IndicoClient) : Query<Submission?, GetSubmissionGraphQL.Result>() {

    private var submissionId = 0


    /**
     * Submission Id
     * @param id submissionId
     * @return GetSubmission
     */
    fun submissionId(id: Int): GetSubmission {
        submissionId = id
        return this
    }

    /**
     * Execute the query
     * @return Submission
     */
    override fun query(): Submission? {
        return try {
            val call = GetSubmissionGraphQL(GetSubmissionGraphQL.Variables(
                submissionId = this.submissionId
            ))

            val response = this.client.execute(call)
            handleErrors(response)
            val submission = response.data?.submission?: return null
            Submission.Builder()
                .id(submission.id!!)
                .datasetId(submission.datasetId!!)
                .workflowId(submission.workflowId!!)
                .status(submission.status?.toString()?.let { SubmissionStatus.valueOf(it) })
                .inputFile(submission.inputFile)
                .inputFilename(submission.inputFilename)
                .resultFile(submission.resultFile)
                .retrieved(submission.retrieved!!)
                .build()
        } catch (ex: RuntimeException) {
            throw IndicoQueryException("Call to get the submission failed", ex)
        }
    }

    /**
     * Refresh the Submission
     * @param obj
     * @return Submission
     */
    override fun refresh(obj: Submission?): Submission? {
        return obj
    }

}