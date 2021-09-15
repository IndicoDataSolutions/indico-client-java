package com.indico.mutations

import com.indico.IndicoKtorClient
import com.indico.exceptions.IndicoMutationException
import com.indico.graphql.WorkflowSubmissionGraphQL
import com.indico.graphql.inputs.FileInput
import org.apache.logging.log4j.LogManager
import org.json.JSONArray
import java.io.IOException
import java.util.*

class WorkflowSubmission(private val client: IndicoKtorClient) : Mutation<List<Int?>?, WorkflowSubmissionGraphQL.Result>() {
    private var files: List<String>? = null
    private var id = 0
    private var duplicationId: UUID? = null
    private var streams: Map<String, ByteArray>? = null
    private val logger = LogManager.getLogger(javaClass)

    /**
     * List of local file paths to submit
     * @param files File paths
     * @return WorkflowSubmission
     */
    fun files(files: List<String>?): WorkflowSubmission {
        this.files = files
        return this
    }

    /**
     *
     * @param streams Dictionary of String identifiers and byte streams to upload.
     * @return
     */
    fun byteStreams(streams: Map<String, ByteArray>?): WorkflowSubmission {
        this.streams = streams
        return this
    }

    /**
     * Id of workflow to submit files to
     * @param id Workflow Id
     * @return WorkflowSubmission
     */
    fun workflowId(id: Int): WorkflowSubmission {
        this.id = id
        return this
    }

    /**
     * A UUID representing a unique set of files and workflow activity.
     * This optional parameter helps the platform detect and prevent duplicate submissions.
     * @param id
     * @return
     */
    fun duplicationId(id: UUID?): WorkflowSubmission {
        duplicationId = id
        return this
    }

    /**
     * Executes request and returns Submissions
     * @return Integer List
     */
    override fun execute(): List<Int?>? {
        var fileMetadata: JSONArray
        val files: MutableList<FileInput> = ArrayList<FileInput>()
        try {
            if (this.files != null) {
                files.addAll(processFiles(this.files!!, client))
            }
            if (streams != null) {
                files.addAll(processStreams(this.streams!!, client))
            }
        } catch (e: IOException) {
            throw IndicoMutationException(e.message, e)
        }
        if (duplicationId == null) {
            duplicationId = UUID.randomUUID()
        }
        return try {
            val call = WorkflowSubmissionGraphQL(WorkflowSubmissionGraphQL.Variables(files = files, workflowId = id, duplicationId = duplicationId.toString()))
            val response = client.execute(call)
            handleErrors(response)
            val workflowSubmission = response.data!!.workflowSubmission!!
            if (workflowSubmission.isDuplicateRequest!!) {
                logger.debug(
                    "Duplicate submission sent for submission ids " + workflowSubmission.submissionIds.toString()
                )
            }
            workflowSubmission.submissionIds
        }catch (ex: java.lang.RuntimeException) {
            throw IndicoMutationException("Call to submit submission failed", ex)
        }
    }
}