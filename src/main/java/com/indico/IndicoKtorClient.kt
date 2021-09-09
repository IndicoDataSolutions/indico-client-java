package com.indico

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.indico.query.ListSubmissions
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit

class IndicoKtorClient(config: IndicoConfig) : Closeable {

    var graphClient: GraphQLKtorClient

    init {
        val serverURL = config.protocol + "://" + config.host;
        val apiUrl = serverURL+"/graph/api/graphql"
        val interceptor = AuthorizationInterceptor(serverURL, config.apiToken, config)
        val retryInterceptor = RetryInterceptor(config)
        try {
            interceptor.refreshAuthState()
        } catch (exception: IOException) {
            throw RuntimeException(exception)
        }

        val preconfigOkHttpClient = OkHttpClient.Builder()
        .authenticator(TokenAuthenticator(interceptor))
            .addInterceptor(interceptor)
            .addInterceptor(retryInterceptor)
            .readTimeout(config.connectionReadTimeout.toLong(), TimeUnit.SECONDS)
            .writeTimeout(config.connectionWriteTimeout.toLong(), TimeUnit.SECONDS)
            .connectTimeout(config.connectTimeout.toLong(), TimeUnit.SECONDS)
            .followRedirects(true)
            .build()

        val okHttpClient = HttpClient(engineFactory = OkHttp) {
            engine {
                config {
                    connectTimeout(config.connectTimeout.toLong(), TimeUnit.SECONDS)
                    readTimeout(60, TimeUnit.SECONDS)
                    writeTimeout(60, TimeUnit.SECONDS)
                }
                preconfigured = preconfigOkHttpClient
            }

        }
        this.graphClient = GraphQLKtorClient(
            url = URL(apiUrl),
            httpClient = okHttpClient
        )

    }
    override fun close() {
        return
    }
    fun <T : Any> execute(
        request: GraphQLClientRequest<T>,
    ): GraphQLClientResponse<T> {

        return runBlocking { graphClient.execute(request) }
    }

    suspend fun <T: Any> executeAsync(request:GraphQLClientRequest<T>,):GraphQLClientResponse<T>{
        return graphClient.execute(request)
    }

    fun listSubmissions(): ListSubmissions? {
        return ListSubmissions(this)
    }
}