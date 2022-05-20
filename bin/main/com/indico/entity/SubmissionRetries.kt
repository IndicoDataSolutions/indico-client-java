package com.indico.entity

import com.indico.type.SubmissionStatus

/**
 * Information about all retries for a submission.
 */
data class SubmissionRetries(val submissionId: Int, val retries: List<SubmissionRetry>, val status: SubmissionStatus)
/**
 * Information about a retry attempt for a given submission.
 */
data class SubmissionRetry(val id: Int, val previousErrors: String?,
val previousStatus: SubmissionStatus?, val retryErrors: String?, val submissionId: Int) {

}