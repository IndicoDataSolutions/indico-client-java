package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.modelgroupprogressgraphqlquery.ModelGroupPage
import kotlin.Int
import kotlin.String
import kotlin.reflect.KClass

public const val MODEL_GROUP_PROGRESS_GRAPH_QL_QUERY: String =
    "query ModelGroupProgressGraphQLQuery(${'$'}id: Int) {\n    modelGroups(modelGroupIds: [${'$'}id]) {\n        modelGroups {\n            models {\n                id\n                status\n                trainingProgress {\n                    percentComplete\n                }\n            }\n        }\n    }\n}"

@Generated
public class ModelGroupProgressGraphQLQuery(
  public override val variables: ModelGroupProgressGraphQLQuery.Variables
) : GraphQLClientRequest<ModelGroupProgressGraphQLQuery.Result> {
  public override val query: String = MODEL_GROUP_PROGRESS_GRAPH_QL_QUERY

  public override val operationName: String = "ModelGroupProgressGraphQLQuery"

  public override fun responseType(): KClass<ModelGroupProgressGraphQLQuery.Result> =
      ModelGroupProgressGraphQLQuery.Result::class

  @Generated
  public data class Variables(
    public val id: OptionalInput<Int> = OptionalInput.Undefined
  )

  @Generated
  public data class Result(
    public val modelGroups: ModelGroupPage?
  )
}
