package com.indico.graphql.modelgroupgraphql

import com.expediagroup.graphql.client.Generated
import com.indico.graphql.enums.ModelStatus
import kotlin.Int
import kotlin.String

@Generated
public data class ModelGroup(
  public val id: Int?,
  public val name: String?,
  public val status: ModelStatus?,
  public val selectedModel: Model?
)
