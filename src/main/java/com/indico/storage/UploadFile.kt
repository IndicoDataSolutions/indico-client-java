package com.indico.storage

import com.indico.IndicoClient
import com.indico.IndicoKtorClient
import com.indico.JSON
import com.indico.RestRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONArray
import java.io.File
import java.io.IOException
import java.util.ArrayList

class UploadFile(private val client: IndicoClient) : RestRequest<JSONArray?> {
    private val files: MutableList<File> = ArrayList()

    /**
     * Files to upload
     *
     * @param filePaths File paths
     * @return UploadFile
     */
    fun filePaths(filePaths: List<String>?): UploadFile {
        for (path in filePaths!!) {
            val file = File(path)
            if (file.exists()) {
                files.add(file)
            } else {
                throw RuntimeException("File $path does not exist")
            }
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
        client as IndicoKtorClient
        val uploadUrl: String = this.client.config.appBaseUrl + "/storage/files/store"
        val multipartBody = MultipartBody.Builder().setType(MultipartBody.FORM)
        for (file in files) {
            multipartBody.addFormDataPart(
                file.name, file.name, file.asRequestBody("application/octet-stream".toMediaTypeOrNull())

            )
        }
        val requestBody: MultipartBody = multipartBody.build()
        val request = Request.Builder()
            .url(uploadUrl)
            .post(requestBody)
            .build()
        val result: Response = this.client.httpClient.newCall(request).execute()
        val body = result.body!!.string()
        val fileMeta = JSON(body).asJSONArray()
        result.close()
        return fileMeta as JSONArray
    }

}