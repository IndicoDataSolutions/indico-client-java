package com.indico.mutations

import com.indico.IndicoClient
import com.indico.IndicoHelper
import com.indico.type.JobStatus
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentExtractionTest {

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
      var files = listOf(rootFolder+"org-sample.pdf", rootFolder+ "pdf1.pdf")

        val docEx = client.documentExtraction()
        docEx?.files(files)
        val jobs = docEx?.execute()
        assertNotNull(jobs)
        val job = jobs?.get(0)!!
        assertNotNull(job)
        while (job.status() === JobStatus.PENDING) {
            job.status()
        }

        assertNotNull(job.resultAsString())
        assertTrue(job.resultAsString().any())


    }

}