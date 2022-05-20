package com.indico.graphql.workflowsubmissiongraphql

import com.expediagroup.graphql.client.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.collections.List

@Generated
public data class SubmissionResult(
  /**
   * Returned if submissions are recorded
   */
  public val submissionIds: List<Int?>?,
  /**
   * Returned if submissions are duplicates
   */
  public val isDuplicateRequest: Boolean?
)
