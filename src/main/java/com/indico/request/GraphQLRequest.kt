package com.indico.request

import com.expediagroup.graphql.client.Generated
import com.indico.IndicoKtorClient
import com.indico.JSON
import com.indico.RestRequest
import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.fasterxml.jackson.databind.JsonSerializer.None
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import com.indico.exceptions.IndicoQueryException
import com.indico.graphql.GET_SUBMISSION_GRAPH_QL
import com.indico.graphql.GetSubmissionGraphQL
import com.indico.entity.Submission
import com.indico.query.Query
import kotlin.reflect.KClass

class RawGraphQLRequest(
    override val query: String,
    override val operationName: String,
    override val variables: Variables,
) : GraphQLClientRequest<RawGraphQLRequest.Result>  {

    public override fun responseType(): KClass<Result> =
        Result::class

    public data class Variables(
        public val variables: ArrayList<String>
    )

    public data class Result(
        public val submission: Submission?
    )
}


class GraphQLRequest(
    private val indicoClient: IndicoKtorClient,
    private val query: String,
    private val operationName: String,
    private val variables: ArrayList<String>,
) : Query<RawGraphQLRequest.Result?, RawGraphQLRequest.Result>() {
    private var requestVariables: RawGraphQLRequest.Variables = RawGraphQLRequest.Variables(variables = variables)

    fun requestVariables(requestVariables: ArrayList<String>): GraphQLRequest {
        this.requestVariables = RawGraphQLRequest.Variables(variables = variables)
        return this
    }

    override fun query(): RawGraphQLRequest.Result? {
        return try {
            val call = RawGraphQLRequest(
                query,
                operationName,
                requestVariables,
            )
            val response = this.indicoClient.execute(call)
            handleErrors(response)
            return response.data
        } catch (ex: RuntimeException) {
            throw IndicoQueryException("Call to get the submission failed", ex)
        }
    }

    override fun refresh(obj: RawGraphQLRequest.Result?): RawGraphQLRequest.Result? {
        return obj
    }
}