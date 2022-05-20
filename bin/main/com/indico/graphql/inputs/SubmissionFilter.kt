package com.indico.graphql.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.indico.graphql.enums.SubmissionStatus
import kotlin.Boolean
import kotlin.String
import kotlin.collections.List

@Generated
public data class SubmissionFilter(
  public val AND: OptionalInput<List<SubmissionFilter?>> = OptionalInput.Undefined,
  public val OR: OptionalInput<List<SubmissionFilter?>> = OptionalInput.Undefined,
  public val ands: OptionalInput<List<SubmissionFilter?>> = OptionalInput.Undefined,
  /**
   * input filename contains
   */
  public val inputFilename: OptionalInput<String> = OptionalInput.Undefined,
  public val ors: OptionalInput<List<SubmissionFilter?>> = OptionalInput.Undefined,
  /**
   * Submission has been marked as having been retrieved
   */
  public val retrieved: OptionalInput<Boolean> = OptionalInput.Undefined,
  /**
   * submission status is
   */
  public val status: OptionalInput<SubmissionStatus> = OptionalInput.Undefined
)
