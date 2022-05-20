package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.loadmodelgraphql.ModelLoad
import kotlin.Int
import kotlin.String
import kotlin.reflect.KClass

public const val LOAD_MODEL_GRAPH_QL: String =
    "mutation LoadModelGraphQL(${'$'}model_id: Int!) {\n  modelLoad(modelId: ${'$'}model_id) {\n    status\n  }\n}"

@Generated
public class LoadModelGraphQL(
  public override val variables: LoadModelGraphQL.Variables
) : GraphQLClientRequest<LoadModelGraphQL.Result> {
  public override val query: String = LOAD_MODEL_GRAPH_QL

  public override val operationName: String = "LoadModelGraphQL"

  public override fun responseType(): KClass<LoadModelGraphQL.Result> =
      LoadModelGraphQL.Result::class

  @Generated
  public data class Variables(
    public val model_id: Int
  )

  @Generated
  public data class Result(
    public val modelLoad: ModelLoad?
  )
}
