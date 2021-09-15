package com.indico.query


import com.indico.IndicoClient
import com.indico.Query
import com.indico.entity.Submission
import com.indico.exceptions.IndicoQueryException
import com.indico.graphql.GetSubmissionGraphQL

class GetSubmission(private val client: IndicoClient) : Query<Submission?> {

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
    override fun query(): Submission {
        return try {
            val call = GetSubmissionGraphQL(GetSubmissionGraphQL.Variables(
                submissionId = this.submissionId
            ))

            val response = this.client.execute(call)
            val submission = response.data!!.submission!!
            Submission.Builder()
                .id(submission.id!!)
                .datasetId(submission.datasetId!!)
                .workflowId(submission.workflowId!!)
                .status(submission.status!!)
                .inputFile(submission.inputFile!!)
                .inputFilename(submission.inputFilename!!)
                .resultFile(submission.resultFile!!)
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