package com.indico.graphql.retrysubmissiongraphql

import com.expediagroup.graphql.client.Generated
import com.indico.graphql.enums.SubmissionStatus
import kotlin.Int
import kotlin.String
import kotlin.collections.List

@Generated
public data class Submission(
  /**
   * Current status of the submission process
   */
  public val status: SubmissionStatus?,
  /**
   * Unique ID of the submission
   */
  public val id: Int?,
  /**
   * Errors occurred during this submission
   */
  public val errors: String?,
  public val retries: List<SubmissionRetry?>?
)
