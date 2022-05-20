package com.indico.graphql

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.jackson.types.OptionalInput.Undefined
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.indico.graphql.documentextractiongraphql.DocumentExtraction
import com.indico.graphql.inputs.FileInput
import kotlin.String
import kotlin.collections.List
import kotlin.reflect.KClass

public const val DOCUMENT_EXTRACTION_GRAPH_QL: String =
    "mutation DocumentExtractionGraphQL(${'$'}files: [FileInput]!, ${'$'}jsonConfig: JSONString){\n    documentExtraction(files: ${'$'}files, jsonConfig: ${'$'}jsonConfig){\n        jobIds\n    }\n}"

@Generated
public class DocumentExtractionGraphQL(
  public override val variables: DocumentExtractionGraphQL.Variables
) : GraphQLClientRequest<DocumentExtractionGraphQL.Result> {
  public override val query: String = DOCUMENT_EXTRACTION_GRAPH_QL

  public override val operationName: String = "DocumentExtractionGraphQL"

  public override fun responseType(): KClass<DocumentExtractionGraphQL.Result> =
      DocumentExtractionGraphQL.Result::class

  @Generated
  public data class Variables(
    public val files: List<FileInput?>,
    public val jsonConfig: OptionalInput<JSONString> = OptionalInput.Undefined
  )

  @Generated
  public data class Result(
    public val documentExtraction: DocumentExtraction?
  )
}
