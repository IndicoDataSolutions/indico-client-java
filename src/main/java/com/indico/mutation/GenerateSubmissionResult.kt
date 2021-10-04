package com.indico.mutation

import com.indico.IndicoClient
import com.indico.entity.Submission
import com.indico.exceptions.IndicoMutationException
import com.indico.graphql.CreateSubmissionResultsGraphQL
import com.indico.query.Job

class GenerateSubmissionResult(private val client: IndicoClient) : Mutation<Job?, CreateSubmissionResultsGraphQL.Result>() {
    private var submissionId = 0

    /**
     * Set the submission ID to fetch the result for.
     */
    fun submission(submissionId: Int): GenerateSubmissionResult {
        this.submissionId = submissionId
        return this
    }

    @Deprecated("Please add the submission id instead.")
    /**
     * Set the submission object to generate submission result for.
     */
    fun submission(submission: Submission): GenerateSubmissionResult {
        submissionId = submission.id
        return this
    }

    override fun execute(): Job? {
        return try {
            val call = CreateSubmissionResultsGraphQL(variables =
            CreateSubmissionResultsGraphQL.Variables(
                submissionId = this.submissionId
            ))

            val response = this.client.execute(call)
            handleErrors(response)
            val submissionResults = response.data?.submissionResults?:
                throw IndicoMutationException("Failed to generate submission results, no submission results returned.")
            val jobId: String = submissionResults.jobId!!
            Job(client, id = jobId, errors = emptyList())
         } catch (ex: RuntimeException) {
            throw IndicoMutationException("Call to generate the submission result failed", ex)
        }
    }

}