package com.indico.graphql.listworkflowsgraphql

import com.expediagroup.graphql.client.Generated
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Int
import kotlin.String

@Generated
public data class Workflow(
  /**
   * Unique ID of the workflow
   */
  public val id: Int?,
  public val name: String?,
  /**
   * DEPRECATED: Status of the Review queue
   */
  @Deprecated(message = "Now uses settings' `review_queue_enabled`")
  public val reviewEnabled: Boolean?
)
