package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.jobstatusgraphql.Job
import kotlin.String
import kotlin.reflect.KClass

public const val JOB_STATUS_GRAPH_QL: String =
    "query JobStatusGraphQL(${'$'}id: String!) {\n  job(id: ${'$'}id) {\n    status\n  }\n}"

@Generated
public class JobStatusGraphQL(
  public override val variables: JobStatusGraphQL.Variables
) : GraphQLClientRequest<JobStatusGraphQL.Result> {
  public override val query: String = JOB_STATUS_GRAPH_QL

  public override val operationName: String = "JobStatusGraphQL"

  public override fun responseType(): KClass<JobStatusGraphQL.Result> =
      JobStatusGraphQL.Result::class

  @Generated
  public data class Variables(
    public val id: String
  )

  @Generated
  public data class Result(
    public val job: Job?
  )
}
