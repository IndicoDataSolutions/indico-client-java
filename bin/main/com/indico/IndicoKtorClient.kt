package com.indico

import com.expediagroup.graphql.client.jackson.GraphQLClientJacksonSerializer
import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.indico.mutation.*
import com.indico.query.*
import com.indico.storage.RetrieveBlob
import com.indico.storage.UploadFile
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Kotlin based concrete implementation of the IndicoClient.
 */
class IndicoKtorClient(val config: IndicoConfig) : Closeable, IndicoClient {

    var graphClient: GraphQLKtorClient
    var httpClient: OkHttpClient

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
        this.httpClient = preconfigOkHttpClient
        this.graphClient = GraphQLKtorClient(
            url = URL(apiUrl),
            httpClient = okHttpClient,
            serializer = GraphQLClientJacksonSerializer()
        )

    }
    override fun close() {
        this.graphClient.close()
        return
    }


    override fun <T : Any> execute(
        request: GraphQLClientRequest<T>,
    ): GraphQLClientResponse<T> {
        return runBlocking { graphClient.execute(request) }
    }

    override suspend fun <T: Any> executeAsync(request:GraphQLClientRequest<T>):GraphQLClientResponse<T>{
        return graphClient.execute(request)
    }

    override fun listSubmissions(): ListSubmissions? {
        return ListSubmissions(this)
    }

    override fun workflowSubmission(): WorkflowSubmission?{
        return WorkflowSubmission(this)
    }

    override fun documentExtraction(): DocumentExtraction{
        return DocumentExtraction(this)
    }

    override fun generateSubmissionResult(): GenerateSubmissionResult? {
        return GenerateSubmissionResult(this)
    }

    override fun modelGroupLoad(): ModelGroupLoad? {
        return ModelGroupLoad(this)
    }

    override fun modelGroupPredict(): ModelGroupPredict? {
        return ModelGroupPredict(this)
    }

    override fun submissionResult(): SubmissionResult? {
        return SubmissionResult(this)
    }

    override fun updateSubmission(): UpdateSubmission? {
        return UpdateSubmission(this)
    }

    override fun getSubmission(): GetSubmission? {
        return GetSubmission(this)
    }

    override fun modelGroupQuery(): ModelGroupQuery? {
        return ModelGroupQuery(this)
    }

    override fun trainingModelWithProgressQuery(): TrainingModelWithProgressQuery? {
        return TrainingModelWithProgressQuery(this)
    }

    override fun retrieveBlob(): RetrieveBlob? {
        return RetrieveBlob(this)
    }

    override fun uploadFile(): UploadFile? {
        return UploadFile(this)
    }

    override fun retrySubmission(): RetrySubmission? {
        return RetrySubmission(this)
    }
}