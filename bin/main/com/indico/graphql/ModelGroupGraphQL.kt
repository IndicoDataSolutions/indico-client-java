package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.modelgroupgraphql.ModelGroupPage
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val MODEL_GROUP_GRAPH_QL: String =
    "query ModelGroupGraphQL(${'$'}modelGroupIds: [Int]!) {\n  modelGroups(modelGroupIds: ${'$'}modelGroupIds) {\n    modelGroups {\n      id\n      name\n      status\n      selectedModel {\n        id\n        status\n      }\n    }\n  }\n}"

@Generated
public class ModelGroupGraphQL(
  public override val variables: ModelGroupGraphQL.Variables
) : GraphQLClientRequest<ModelGroupGraphQL.Result> {
  public override val query: String = MODEL_GROUP_GRAPH_QL

  public override val operationName: String = "ModelGroupGraphQL"

  public override fun responseType(): KClass<ModelGroupGraphQL.Result> =
      ModelGroupGraphQL.Result::class

  @Generated
  public data class Variables(
    public val modelGroupIds: List<Int?>
  )

  @Generated
  public data class Result(
    public val modelGroups: ModelGroupPage?
  )
}
