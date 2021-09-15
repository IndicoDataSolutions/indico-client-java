package com.indico.storage

import com.indico.IndicoKtorClient
import com.indico.JSON
import com.indico.RestRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import okhttp3.MultipartBody
import okhttp3.Request
import java.util.UUID
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray

class UploadStream(private val client: IndicoKtorClient) : RestRequest<JSONArray?> {
    private var bytes: Map<String, ByteArray>? = null
    private var fileName: String? = null

    /**
     * A stream to upload.
     *
     * @param stream Dictionary of string identifiers (ideally file names) to byte[] streams
     * @return UploadFile
     */
    fun byteStream(stream: Map<String, ByteArray>?): UploadStream {
        if (stream != null) {
            bytes = stream
        }
        return this
    }

    /**
     * Upload files and return metadata
     *
     * @return JSONArray
     * @throws IOException
     */
    @Throws(IOException::class)
    override fun call(): JSONArray {
        val uploadUrl = client.config.appBaseUrl + "/storage/files/store"
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
        if (fileName == null || fileName!!.isEmpty()) {
            fileName = UUID.randomUUID().toString()
        }
        for (key in bytes!!.keys) {
            multipartBody.addFormDataPart(
                key, key,
               bytes!![key]!!.toRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
        }
        val requestBody: MultipartBody = multipartBody.build()
        val request = Request.Builder()
            .url(uploadUrl)
            .post(requestBody)
            .build()
        val result = client.httpClient.newCall(request).execute()
        val body = result.body!!.string()
        val fileMeta = JSON(body).asJSONArray()
        result.close()
        return fileMeta
    }
}