package com.indico.request

import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.indico.IndicoKtorClient
import com.indico.exceptions.IndicoQueryException
import com.indico.query.Query
import org.json.JSONObject
import kotlin.reflect.KClass


class RawGraphQLRequest(
    override val query: String,
    override val operationName: String,
    override val variables: JsonNode,
) : GraphQLClientRequest<JsonNode>  {
    public override fun responseType(): KClass<JsonNode> =
        JsonNode::class
}


class GraphQLRequest(
    private val indicoClient: IndicoKtorClient,
    private val query: String,
    private val operationName: String,
    private val variables: JsonNode,
) : Query<JSONObject?, JsonNode>() {

    override fun query(): JSONObject? {
         try {
            val call = RawGraphQLRequest(
                query,
                operationName,
                variables,
            )
            val response = this.indicoClient.execute(call)
            handleErrors(response)
            val objectMapper = ObjectMapper()
            val responseAsString = objectMapper.writeValueAsString(response.data)
            return JSONObject(responseAsString)
        } catch (ex: RuntimeException) {
            throw IndicoQueryException("Call to get the submission failed", ex)
        }
    }

    override fun refresh(obj: JSONObject?): JSONObject? {
        return obj
    }
}