package com.indico.mutation

import com.indico.IndicoClient
import com.indico.entity.Submission
import com.indico.entity.SubmissionRetries
import com.indico.entity.SubmissionRetry
import com.indico.exceptions.IndicoMutationException
import com.indico.graphql.RetrySubmissionGraphQL
import com.indico.type.SubmissionStatus
import java.util.function.Consumer

/**
 * Executes a retry for each of a given list of submission Ids.
 */
class RetrySubmission(private val client: IndicoClient):
    Mutation<List<SubmissionRetries>?, RetrySubmissionGraphQL.Result>() {

    private var ids: List<Int> = emptyList();
    /**
     * An id of a submission to retry.
     * @param ids
     * @return RetrySubmission
     */
    fun ids(ids: List<Int>): RetrySubmission{
        this.ids = ids
        return this
    }

    override fun execute(): List<SubmissionRetries>? {
        if(ids.isEmpty()){
            throw IndicoMutationException("Must Provide List of Submission Ids to retry")
        }
        return try{
            val call = RetrySubmissionGraphQL(
                RetrySubmissionGraphQL.Variables(
                    submissionIds = ids
                )
            )
            val response = client.execute(call)
            handleErrors(response)
            var retryResult = response.data?.retrySubmissions?:
                throw IndicoMutationException("Could not retrieve any submissions")
            var result = ArrayList<SubmissionRetries>()
            for (retry in retryResult){
                val subRetries = ArrayList<SubmissionRetry>()
                retry?.retries?.forEach(Consumer { retrygraphql ->
                    if(retrygraphql != null)
                    {
                        val status = retrygraphql.previousStatus.toString().let{SubmissionStatus.valueOf(it)}
                        subRetries.add(
                        SubmissionRetry(id = retrygraphql.id!!, previousErrors = retrygraphql.previousErrors,
                        previousStatus = status, retryErrors = retrygraphql.retryErrors, submissionId = retrygraphql.submissionId!!)
                    )
                    }
                })
                retry?: continue
                val currStatus = retry.status.toString().let { SubmissionStatus.valueOf(it) }
                val subRetry = SubmissionRetries(submissionId = retry.id!!, status = currStatus,
                    retries = subRetries )
                result.add(subRetry)
            }
            result
        } catch(ex: RuntimeException){
            throw IndicoMutationException("Failed to complete the retry request", ex)
        }
    }
}
