package com.indico.mutation

import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.indico.IndicoClient
import com.indico.exceptions.IndicoMutationException
import com.indico.graphql.WorkflowSubmissionGraphQL
import com.indico.graphql.inputs.FileInput
import com.indico.graphql.enums.SubmissionResultVersion as GraphQlResultVersion
import com.indico.type.SubmissionResultVersion
import org.apache.logging.log4j.LogManager
import java.io.IOException
import java.util.*

class WorkflowSubmission(private val client: IndicoClient) : Mutation<List<Int?>?, WorkflowSubmissionGraphQL.Result>() {
    private var files: List<String>? = null
    private var id = 0
    private var duplicationId: UUID? = null
    private var streams: Map<String, ByteArray>? = null
    private var resultFileVersion: SubmissionResultVersion? = null
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

    fun resultFileVersion(version: SubmissionResultVersion?): WorkflowSubmission{
        resultFileVersion = version
        return this
    }

    /**
     * Executes request and returns Submissions
     * @return Integer List
     */
    override fun execute(): List<Int?>? {
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

        val resultVersion =
            if(resultFileVersion != null)
                OptionalInput.Defined(GraphQlResultVersion.valueOf(resultFileVersion.toString()))
            else OptionalInput.Undefined

        return try {
            val call = WorkflowSubmissionGraphQL(
                WorkflowSubmissionGraphQL.Variables(
                    files = files, workflowId = id, duplicationId = OptionalInput.Defined(duplicationId.toString()),
                    resultVersion = resultVersion
               ))
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
