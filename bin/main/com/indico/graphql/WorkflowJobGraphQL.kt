package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.inputs.FileInput
import com.indico.graphql.workflowjobgraphql.SubmissionResult
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val WORKFLOW_JOB_GRAPH_QL: String =
    "mutation WorkflowJobGraphQL(${'$'}workflowId: Int!, ${'$'}files: [FileInput]!, ${'$'}recordSubmission: Boolean) {\n    workflowSubmission(workflowId: ${'$'}workflowId, files: ${'$'}files, recordSubmission: ${'$'}recordSubmission) {\n        jobIds\n    }\n}"

@Generated
public class WorkflowJobGraphQL(
  public override val variables: WorkflowJobGraphQL.Variables
) : GraphQLClientRequest<WorkflowJobGraphQL.Result> {
  public override val query: String = WORKFLOW_JOB_GRAPH_QL

  public override val operationName: String = "WorkflowJobGraphQL"

  public override fun responseType(): KClass<WorkflowJobGraphQL.Result> =
      WorkflowJobGraphQL.Result::class

  @Generated
  public data class Variables(
    public val workflowId: Int,
    public val files: List<FileInput?>,
    public val recordSubmission: OptionalInput<Boolean> = OptionalInput.Undefined
  )

  @Generated
  public data class Result(
    public val workflowSubmission: SubmissionResult?
  )
}
