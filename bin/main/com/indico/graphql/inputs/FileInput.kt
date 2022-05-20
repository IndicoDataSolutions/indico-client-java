package com.indico.graphql.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.indico.graphql.JSONString
import kotlin.String

@Generated
public data class FileInput(
  public val filemeta: OptionalInput<JSONString> = OptionalInput.Undefined,
  public val filename: OptionalInput<String> = OptionalInput.Undefined
)
