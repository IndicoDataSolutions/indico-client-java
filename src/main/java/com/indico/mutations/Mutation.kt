package com.indico.mutations

import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.indico.IndicoClient
import com.indico.exceptions.IndicoMutationException
import com.indico.graphql.inputs.FileInput
import com.indico.storage.UploadFile
import com.indico.storage.UploadStream
import org.json.JSONArray
import org.json.JSONObject
import java.lang.StringBuilder
import java.util.ArrayList

sealed class Mutation<T,R> {
    /**
     * Executes the graphql query and returns the results as a specific type
     *
     * @return result of query of type T
     */
     abstract fun execute(): T?

    /**
     * Raises IndicoMutationError if there are errors.
     */
    protected fun handleErrors(response: GraphQLClientResponse<R>) {
        if (response.errors != null && response.errors!!.isNotEmpty()) {
            val errors = StringBuilder()
            for (err in response.errors!!.asIterable()) {
                errors.append(err.toString() + "\n")
            }
            throw IndicoMutationException(errors.toString())
        }
    }

    protected fun processFiles(fileList: List<String>, client: IndicoClient): List<FileInput>{
        val files: MutableList<FileInput> = ArrayList<FileInput>()
        return try {
            val fileMetadata = uploadFiles(fileList, client)
            for (f in fileMetadata) {
                val uploadMeta = f as JSONObject
                val meta = JSONObject()
                meta.put("name", uploadMeta.getString("name"))
                meta.put("path", uploadMeta.getString("path"))
                meta.put("upload_type", uploadMeta.getString("upload_type"))
                val input = FileInput(filename = f.getString("name"), filemeta = meta.toString())
                files.add(input)
            }
            files
        } catch (ex: RuntimeException){
            throw  IndicoMutationException("Failed to upload files for extraction", ex)
        }
    }

    protected fun processStreams(fileList: Map<String, ByteArray>?, client: IndicoClient): List<FileInput>{
        val files: MutableList<FileInput> = ArrayList<FileInput>()
        return try {
            val fileMetadata = uploadBytes(fileList, client)
            for (f in fileMetadata) {
                val uploadMeta = f as JSONObject
                val meta = JSONObject()
                meta.put("name", uploadMeta.getString("name"))
                meta.put("path", uploadMeta.getString("path"))
                meta.put("upload_type", uploadMeta.getString("upload_type"))
                val input = FileInput(filename = f.getString("name"), filemeta = meta.toString())
                files.add(input)
            }
            files
        } catch (ex: RuntimeException){
            throw  IndicoMutationException("Failed to upload files for extraction", ex)
        }
    }
    private fun uploadFiles(filePaths: List<String>?, client: IndicoClient): JSONArray {
        val uploadRequest = UploadFile(client)
        return uploadRequest.filePaths(filePaths).call()
    }

    private fun uploadBytes(stream: Map<String, ByteArray>?, client: IndicoClient): JSONArray {
        val uploadRequest = UploadStream(client)
        return uploadRequest.byteStream(stream).call()
    }
}