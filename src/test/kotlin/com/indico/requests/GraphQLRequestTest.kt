package com.indico.requests

import com.fasterxml.jackson.databind.ObjectMapper
import com.indico.IndicoClient
import com.indico.IndicoHelper
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GraphQLRequestTest {
    private lateinit var client: IndicoClient

    @BeforeAll
    fun setUp() {
        val helper = IndicoHelper()
        this.client = helper.getIndico()
    }

    @AfterEach
    fun tearDown() {
    }

    @Test
    fun queryListWorkflows() {
        /**
         * Raw GraphQL Query for ListWorkflows
         */
        val query =
            """
            query ListWorkflowsGraphQL(${"$"}datasetIds: [Int], ${"$"}workflowIds: [Int])
            {
                workflows(datasetIds: ${"$"}datasetIds, workflowIds: ${"$"}workflowIds)
                {
                    workflows
                    {
                        id
                        name
                    }
                }
            }
            """
        val operationName = "ListWorkflowsGraphQL"
        val json = "{ \"datasetIds\": [76663], \"workflowIds\": [18838] }"
        val objectMapper = ObjectMapper()
        val variables = objectMapper.readTree(json)
        val graphQLRequest = this.client.rawGraphQLQuery(query, operationName, variables)!!
        val result = graphQLRequest.query()
        assertNotNull(result)
    }
    @Test
    fun queryListSubmissions() {
        /**
         * Raw GraphQL Query for ListSubmissions
         */
        val query =
            """
            query ListSubmissionsGraphQL(${"$"}submissionIds: [Int], ${"$"}workflowIds: [Int], ${"$"}filters: SubmissionFilter, ${"$"}limit: Int)
            {
                submissions(submissionIds: ${"$"}submissionIds, workflowIds: ${"$"}workflowIds, filters: ${"$"}filters, limit: ${"$"}limit)
                {
                    submissions
                    {
                        id
                        datasetId
                        workflowId
                        status
                        inputFile
                        inputFilename
                        resultFile
                    }
                }
            }
        """
        val operationName = "ListSubmissionsGraphQL"
        val json = "{ \"submissionIds\": [], \"workflowIds\": [], \"limit\": 10 }"
        val objectMapper = ObjectMapper()
        val variables = objectMapper.readTree(json)
        val graphQLRequest = this.client.rawGraphQLQuery(query, operationName, variables)!!
        val result = graphQLRequest.query()
        assertNotNull(result)
    }
}