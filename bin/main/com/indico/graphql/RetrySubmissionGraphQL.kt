package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.retrysubmissiongraphql.Submission
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val RETRY_SUBMISSION_GRAPH_QL: String =
    "mutation RetrySubmissionGraphQL(${'$'}submissionIds:[Int]!){\n  retrySubmissions(submissionIds: ${'$'}submissionIds){\n    status\n    id\n    errors\n    retries{\n      id\n      previousErrors\n      previousStatus\n      retryErrors\n      submissionId\n    }\n  }\n}"

@Generated
public class RetrySubmissionGraphQL(
  public override val variables: RetrySubmissionGraphQL.Variables
) : GraphQLClientRequest<RetrySubmissionGraphQL.Result> {
  public override val query: String = RETRY_SUBMISSION_GRAPH_QL

  public override val operationName: String = "RetrySubmissionGraphQL"

  public override fun responseType(): KClass<RetrySubmissionGraphQL.Result> =
      RetrySubmissionGraphQL.Result::class

  @Generated
  public data class Variables(
    public val submissionIds: List<Int?>
  )

  @Generated
  public data class Result(
    public val retrySubmissions: List<Submission?>?
  )
}
