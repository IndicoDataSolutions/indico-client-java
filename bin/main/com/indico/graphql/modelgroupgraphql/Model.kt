package com.indico.graphql.modelgroupgraphql

import com.expediagroup.graphql.client.Generated
import com.indico.graphql.enums.ModelStatus
import kotlin.Int

@Generated
public data class Model(
  public val id: Int?,
  public val status: ModelStatus?
)
