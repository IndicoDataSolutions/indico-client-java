package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.getsubmissiongraphql.Submission
import kotlin.Int
import kotlin.String
import kotlin.reflect.KClass

public const val GET_SUBMISSION_GRAPH_QL: String =
    "query GetSubmissionGraphQL(${'$'}submissionId: Int!){\n    submission(id: ${'$'}submissionId){\n        id\n        datasetId\n        workflowId\n        status\n        inputFile\n        inputFilename\n        resultFile\n        retrieved\n    }\n}"

@Generated
public class GetSubmissionGraphQL(
  public override val variables: GetSubmissionGraphQL.Variables
) : GraphQLClientRequest<GetSubmissionGraphQL.Result> {
  public override val query: String = GET_SUBMISSION_GRAPH_QL

  public override val operationName: String = "GetSubmissionGraphQL"

  public override fun responseType(): KClass<GetSubmissionGraphQL.Result> =
      GetSubmissionGraphQL.Result::class

  @Generated
  public data class Variables(
    public val submissionId: Int
  )

  @Generated
  public data class Result(
    public val submission: Submission?
  )
}
