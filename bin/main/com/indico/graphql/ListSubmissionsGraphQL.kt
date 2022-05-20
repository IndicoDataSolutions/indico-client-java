package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.inputs.SubmissionFilter
import com.indico.graphql.listsubmissionsgraphql.SubmissionPage
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val LIST_SUBMISSIONS_GRAPH_QL: String =
    "query ListSubmissionsGraphQL(\n    ${'$'}submissionIds: [Int],\n    ${'$'}workflowIds: [Int],\n    ${'$'}filters: SubmissionFilter,\n    ${'$'}limit: Int\n){\n    submissions(\n        submissionIds: ${'$'}submissionIds,\n        workflowIds: ${'$'}workflowIds,\n        filters: ${'$'}filters,\n        limit: ${'$'}limit\n    ){\n        submissions {\n            id\n            datasetId\n            workflowId\n            status\n            inputFile\n            inputFilename\n            resultFile\n        }\n    }\n}"

@Generated
public class ListSubmissionsGraphQL(
  public override val variables: ListSubmissionsGraphQL.Variables
) : GraphQLClientRequest<ListSubmissionsGraphQL.Result> {
  public override val query: String = LIST_SUBMISSIONS_GRAPH_QL

  public override val operationName: String = "ListSubmissionsGraphQL"

  public override fun responseType(): KClass<ListSubmissionsGraphQL.Result> =
      ListSubmissionsGraphQL.Result::class

  @Generated
  public data class Variables(
    public val submissionIds: OptionalInput<List<Int?>> = OptionalInput.Undefined,
    public val workflowIds: OptionalInput<List<Int?>> = OptionalInput.Undefined,
    public val filters: OptionalInput<SubmissionFilter> = OptionalInput.Undefined,
    public val limit: OptionalInput<Int> = OptionalInput.Undefined
  )

  @Generated
  public data class Result(
    public val submissions: SubmissionPage?
  )
}
