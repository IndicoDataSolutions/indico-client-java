package com.indico.mutations
import com.indico.IndicoClient
import com.indico.IndicoHelper
import com.indico.entity.Submission
import com.indico.mutation.WorkflowSubmission
import com.indico.storage.Blob
import com.indico.type.SubmissionStatus
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.file.Files
import kotlin.io.path.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class WorkflowSubmissionTest {

    private lateinit var client: IndicoClient
    private lateinit var rootFolder: String
    private var workflowId: Int = 0



    @BeforeAll
    fun setUp() {
        val helper = IndicoHelper()
        this.client = helper.getIndico()
        rootFolder = helper.getDatasetLocation()
        workflowId = helper.getWorkflowId()

    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun execute() {
        var workflowMutation = WorkflowSubmission(client)

        var files = listOf(rootFolder+"org-sample.pdf", rootFolder+ "pdf1.pdf")
        var results = workflowMutation.workflowId(workflowId).files(files).execute()

        assertNotNull(results)
        results?.let { assert(it.isNotEmpty()) }
        assertTrue(results!!.any())


    }

    @Test
    fun generateAndRetrieveResult(){
        var workflowMutation = WorkflowSubmission(client)

        var files = listOf(rootFolder+"org-sample.pdf", rootFolder+ "pdf1.pdf")
        var results = workflowMutation.workflowId(workflowId).files(files).execute()

        assertNotNull(results)
        results?.let { assert(it.isNotEmpty()) }
        assertTrue(results!!.any())

        var submission = client.getSubmission()
        assertNotNull(results[0])
        submission?.submissionId(results[0]!!)
        assertNotNull(submission)
        var result = submission!!.query()
        while (result!!.status !== SubmissionStatus.COMPLETE && result!!.status !== SubmissionStatus.FAILED) {
            result = submission.query()
        }
        assertTrue(result!!.status == SubmissionStatus.COMPLETE)
        process_result(result, client)
    }

    @Test
    fun executeWithBytes()
    {
        var workflowMutation = WorkflowSubmission(client)

        var files = Path(rootFolder+"org-sample.pdf")
        val data = Files.readAllBytes(files)
        val streamMap: MutableMap<String, ByteArray> = HashMap()
        streamMap["org-sample.pdf"] = data

        var results = workflowMutation.workflowId(workflowId).byteStreams(streamMap).execute()

        assertNotNull(results)
        results?.let { assert(it.isNotEmpty()) }
        assertTrue(results!!.any())
    }
    @Test
    fun executeAndRetry(){
        var workflowMutation = WorkflowSubmission(client)

        var files = listOf(rootFolder+"org-sample.pdf", rootFolder+ "pdf1.pdf")
        var results = workflowMutation.workflowId(workflowId).files(files).execute()

        assertNotNull(results)
        results?.let { assert(it.isNotEmpty()) }
        assertTrue(results!!.any())

        var submission = client.getSubmission()
        assertNotNull(results[0])
        submission?.submissionId(results[0]!!)
        assertNotNull(submission)

        Thread.sleep(200)
        val retry = client.retrySubmission()
        retry!!.ids(listOf(results[0]!!))
        retry.execute()


    }

    @Throws(Exception::class)
    fun process_result(submission: Submission, indicoClient: IndicoClient) {
        var blob: Blob? = null
        try {
            val url = "https://" + IndicoHelper().getEnvUrl()  + "/" + submission.resultFile
            val retStorageObj = indicoClient.retrieveBlob()
            retStorageObj!!.url(url)
            blob = retStorageObj.execute()
            assertNotNull(blob)
            blob.close()
            val updateSub = indicoClient.updateSubmission()
            updateSub!!.submissionId(submission.id)
            updateSub.retrieved(true)
            updateSub.execute()
        } catch(ex: Exception){
            throw ex
        } finally {
            blob?.close()
        }
    }
}