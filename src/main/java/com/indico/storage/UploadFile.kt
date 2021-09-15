package com.indico.storage

import com.indico.IndicoClient
import com.indico.JSON
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.File
import java.util.ArrayList

class UploadFile(client: IndicoKtorClient) : RestRequest<JSONArray?> {
    private val client: IndicoKtorClient
    private val files: MutableList<File> = ArrayList()

    /**
     * Files to upload
     *
     * @param filePaths File paths
     * @return UploadFile
     */
    fun filePaths(filePaths: List<String>): UploadFile {
        for (path in filePaths) {
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
        val uploadUrl: String = client.config.getAppBaseUrl() + "/storage/files/store"
        val multipartBody: Builder = Builder().setType(MultipartBody.FORM)
        for (file in files) {
            multipartBody.addFormDataPart(
                file.name, file.name,
                RequestBody.create(parse.parse("application/octet-stream"), file)
            )
        }
        val requestBody: MultipartBody = multipartBody.build()
        val request: Request = Builder()
            .url(uploadUrl)
            .post(requestBody)
            .build()
        val result: Response = client.httpClient.newCall(request).execute()
        val body = result.body!!.string()
        val fileMeta = JSON(body).asJSONArray()
        result.close()
        return fileMeta as JSONArray
    }

    init {
        this.client = client
    }
}