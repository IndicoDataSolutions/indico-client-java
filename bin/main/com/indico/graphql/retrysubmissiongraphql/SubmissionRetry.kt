package com.indico.graphql.retrysubmissiongraphql

import com.expediagroup.graphql.client.Generated
import com.indico.graphql.enums.SubmissionStatus
import kotlin.Int
import kotlin.String

@Generated
public data class SubmissionRetry(
  /**
   * Unique ID of the submission retry
   */
  public val id: Int?,
  /**
   * Errors from previous submission
   */
  public val previousErrors: String?,
  /**
   * Status of submission before it was retried
   */
  public val previousStatus: SubmissionStatus?,
  /**
   * Errors that occurred during the retrying of this submission
   */
  public val retryErrors: String?,
  /**
   * Unique ID of the associated submission
   */
  public val submissionId: Int?
)
