package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.predictmodelgraphql.ModelPredict
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val PREDICT_MODEL_GRAPH_QL: String =
    "mutation PredictModelGraphQL(${'$'}modelId: Int!, ${'$'}data: [String]!) {\n  modelPredict(modelId: ${'$'}modelId, data: ${'$'}data) {\n    jobId\n  }\n}"

@Generated
public class PredictModelGraphQL(
  public override val variables: PredictModelGraphQL.Variables
) : GraphQLClientRequest<PredictModelGraphQL.Result> {
  public override val query: String = PREDICT_MODEL_GRAPH_QL

  public override val operationName: String = "PredictModelGraphQL"

  public override fun responseType(): KClass<PredictModelGraphQL.Result> =
      PredictModelGraphQL.Result::class

  @Generated
  public data class Variables(
    public val modelId: Int,
    public val `data`: List<String?>
  )

  @Generated
  public data class Result(
    public val modelPredict: ModelPredict?
  )
}
