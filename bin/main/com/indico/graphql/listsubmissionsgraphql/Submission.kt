package com.indico.graphql.listsubmissionsgraphql

import com.expediagroup.graphql.client.Generated
import com.indico.graphql.enums.SubmissionStatus
import kotlin.Int
import kotlin.String

@Generated
public data class Submission(
  /**
   * Unique ID of the submission
   */
  public val id: Int?,
  /**
   * ID of the dataset associated with the submission
   */
  public val datasetId: Int?,
  /**
   * ID of the workflow associated with the submission
   */
  public val workflowId: Int?,
  /**
   * Current status of the submission process
   */
  public val status: SubmissionStatus?,
  /**
   * Local URL to first stored input
   */
  public val inputFile: String?,
  /**
   * Original name of first file
   */
  public val inputFilename: String?,
  /**
   * Local URL to stored output
   */
  public val resultFile: String?
)
