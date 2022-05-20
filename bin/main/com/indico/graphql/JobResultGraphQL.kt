package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.jobresultgraphql.Job
import kotlin.String
import kotlin.reflect.KClass

public const val JOB_RESULT_GRAPH_QL: String =
    "query JobResultGraphQL(${'$'}id: String!) {\n  job(id: ${'$'}id) {\n    status\n    result\n  }\n}"

@Generated
public class JobResultGraphQL(
  public override val variables: JobResultGraphQL.Variables
) : GraphQLClientRequest<JobResultGraphQL.Result> {
  public override val query: String = JOB_RESULT_GRAPH_QL

  public override val operationName: String = "JobResultGraphQL"

  public override fun responseType(): KClass<JobResultGraphQL.Result> =
      JobResultGraphQL.Result::class

  @Generated
  public data class Variables(
    public val id: String
  )

  @Generated
  public data class Result(
    public val job: Job?
  )
}
