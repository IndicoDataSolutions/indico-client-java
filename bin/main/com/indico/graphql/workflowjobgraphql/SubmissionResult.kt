package com.indico.graphql.workflowjobgraphql

import com.expediagroup.graphql.client.Generated
import kotlin.String
import kotlin.collections.List

@Generated
public data class SubmissionResult(
  /**
   * Returned if submissions are not recorded
   */
  public val jobIds: List<String?>?
)
