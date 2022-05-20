package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.updatesubmissiongraphql.Submission
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.reflect.KClass

public const val UPDATE_SUBMISSION_GRAPH_QL: String =
    "mutation UpdateSubmissionGraphQL(${'$'}submissionId: Int!, ${'$'}retrieved: Boolean) {\n    updateSubmission(submissionId: ${'$'}submissionId, retrieved: ${'$'}retrieved) {\n        id\n        datasetId\n        workflowId\n        status\n        inputFile\n        inputFilename\n        resultFile\n        retrieved\n    }\n}"

@Generated
public class UpdateSubmissionGraphQL(
  public override val variables: UpdateSubmissionGraphQL.Variables
) : GraphQLClientRequest<UpdateSubmissionGraphQL.Result> {
  public override val query: String = UPDATE_SUBMISSION_GRAPH_QL

  public override val operationName: String = "UpdateSubmissionGraphQL"

  public override fun responseType(): KClass<UpdateSubmissionGraphQL.Result> =
      UpdateSubmissionGraphQL.Result::class

  @Generated
  public data class Variables(
    public val submissionId: Int,
    public val retrieved: OptionalInput<Boolean> = OptionalInput.Undefined
  )

  @Generated
  public data class Result(
    public val updateSubmission: Submission?
  )
}
