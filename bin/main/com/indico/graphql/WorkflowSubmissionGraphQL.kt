package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.enums.SubmissionResultVersion
import com.indico.graphql.inputs.FileInput
import com.indico.graphql.workflowsubmissiongraphql.SubmissionResult
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val WORKFLOW_SUBMISSION_GRAPH_QL: String =
    "mutation WorkflowSubmissionGraphQL(${'$'}duplicationId: String, ${'$'}workflowId: Int!, ${'$'}files: [FileInput]!, ${'$'}resultVersion:SubmissionResultVersion) {\n    workflowSubmission(duplicationId: ${'$'}duplicationId, workflowId: ${'$'}workflowId, files: ${'$'}files, resultVersion: ${'$'}resultVersion) {\n        submissionIds\n        isDuplicateRequest\n    }\n}"

@Generated
public class WorkflowSubmissionGraphQL(
  public override val variables: WorkflowSubmissionGraphQL.Variables
) : GraphQLClientRequest<WorkflowSubmissionGraphQL.Result> {
  public override val query: String = WORKFLOW_SUBMISSION_GRAPH_QL

  public override val operationName: String = "WorkflowSubmissionGraphQL"

  public override fun responseType(): KClass<WorkflowSubmissionGraphQL.Result> =
      WorkflowSubmissionGraphQL.Result::class

  @Generated
  public data class Variables(
    public val duplicationId: OptionalInput<String> = OptionalInput.Undefined,
    public val workflowId: Int,
    public val files: List<FileInput?>,
    public val resultVersion: OptionalInput<SubmissionResultVersion> = OptionalInput.Undefined
  )

  @Generated
  public data class Result(
    public val workflowSubmission: SubmissionResult?
  )
}
