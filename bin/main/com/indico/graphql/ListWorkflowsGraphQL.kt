package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.listworkflowsgraphql.WorkflowPage
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val LIST_WORKFLOWS_GRAPH_QL: String =
    "query ListWorkflowsGraphQL(${'$'}datasetIds: [Int], ${'$'}workflowIds: [Int]){\n    workflows(datasetIds: ${'$'}datasetIds, workflowIds: ${'$'}workflowIds){\n        workflows {\n            id\n            name\n            reviewEnabled\n        }\n    }\n}"

@Generated
public class ListWorkflowsGraphQL(
  public override val variables: ListWorkflowsGraphQL.Variables
) : GraphQLClientRequest<ListWorkflowsGraphQL.Result> {
  public override val query: String = LIST_WORKFLOWS_GRAPH_QL

  public override val operationName: String = "ListWorkflowsGraphQL"

  public override fun responseType(): KClass<ListWorkflowsGraphQL.Result> =
      ListWorkflowsGraphQL.Result::class

  @Generated
  public data class Variables(
    public val datasetIds: OptionalInput<List<Int?>> = OptionalInput.Undefined,
    public val workflowIds: OptionalInput<List<Int?>> = OptionalInput.Undefined
  )

  @Generated
  public data class Result(
    public val workflows: WorkflowPage?
  )
}
