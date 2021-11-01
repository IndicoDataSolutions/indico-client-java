package com.indico.mutation


import com.indico.IndicoClient
import com.indico.RetryInterceptor
import com.indico.entity.Submission
import com.indico.exceptions.IndicoBaseException
import com.indico.exceptions.IndicoMutationException
import com.indico.exceptions.IndicoQueryException
import com.indico.graphql.CreateSubmissionResultsGraphQL
import com.indico.graphql.enums.SubmissionStatus as GraphQlSubmissionStatus
import com.indico.query.GetSubmission
import com.indico.query.Job
import com.indico.type.SubmissionStatus
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

class SubmissionResult(private val client: IndicoClient, private val logger: Logger? = LogManager.getLogger(
    SubmissionResult::class.java
)) : Mutation<Job?, CreateSubmissionResultsGraphQL.Result>() {
    private var submissionId = 0
    private var checkStatus: SubmissionStatus? = null

    /**
     * Id of the submission
     * @param submissionId submission Id
     * @return SubmissionResult
     */
    fun submission(submissionId: Int): SubmissionResult {
        this.submissionId = submissionId
        return this
    }

    fun submission(submission: Submission): SubmissionResult {
        submissionId = submission.id
        return this
    }

    fun checkStatus(checkStatus: SubmissionStatus?): SubmissionResult {
        this.checkStatus = checkStatus
        return this
    }

    /**
     * Generate a result file for a Submission
     * @return Job
     */
    override fun execute(): Job? {
        return try {
            val getSubmission = GetSubmission(client).submissionId(submissionId)
            var submission: Submission? = getSubmission.query()?:
            throw IndicoQueryException("Could not find submission with this id.")
            while (!statusCheck(submission!!.status)) {
                submission = getSubmission.query()
                try {
                    Thread.sleep(1000)
                } catch (exc: InterruptedException) {
                    throw IndicoMutationException("Interrupted while waiting for submission", exc)
                }
            }
            if (!statusCheck(submission.status)) {
                logger?.error("Request timed out while trying to fetch for the status update")
                throw IndicoMutationException("Request timed out")
            } else if (submission.status == null) {
                logger?.error("Submission status returned an unknown value.")
                throw IndicoMutationException("Submission $submissionId does not meet status requirements (unknown status)")
            }
            val generateSubmissionResult: GenerateSubmissionResult = GenerateSubmissionResult(client)
                .submission(submissionId)
            generateSubmissionResult.execute()
        } catch(ex: IndicoBaseException){
            logger?.error(ex)
            throw ex
        }
        catch (ex: RuntimeException) {
            logger?.error(ex)
            throw IndicoMutationException("Call to get submission result failed", ex)
        }
    }

    private fun statusCheck(status: SubmissionStatus): Boolean {
        return if (checkStatus != null) {
            status == checkStatus
        } else status != SubmissionStatus.PROCESSING
    }

}