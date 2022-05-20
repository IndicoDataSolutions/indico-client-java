package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.createsubmissionresultsgraphql.SubmissionResults
import kotlin.Int
import kotlin.String
import kotlin.reflect.KClass

public const val CREATE_SUBMISSION_RESULTS_GRAPH_QL: String =
    "mutation CreateSubmissionResultsGraphQL(${'$'}submissionId: Int!) {\n    submissionResults(submissionId: ${'$'}submissionId) {\n        jobId\n    }\n}"

@Generated
public class CreateSubmissionResultsGraphQL(
  public override val variables: CreateSubmissionResultsGraphQL.Variables
) : GraphQLClientRequest<CreateSubmissionResultsGraphQL.Result> {
  public override val query: String = CREATE_SUBMISSION_RESULTS_GRAPH_QL

  public override val operationName: String = "CreateSubmissionResultsGraphQL"

  public override fun responseType(): KClass<CreateSubmissionResultsGraphQL.Result> =
      CreateSubmissionResultsGraphQL.Result::class

  @Generated
  public data class Variables(
    public val submissionId: Int
  )

  @Generated
  public data class Result(
    public val submissionResults: SubmissionResults?
  )
}
