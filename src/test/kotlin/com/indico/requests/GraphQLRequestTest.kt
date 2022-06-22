package com.indico.requests

import com.fasterxml.jackson.databind.ObjectMapper
import com.indico.IndicoClient
import com.indico.IndicoHelper
import com.indico.main
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
    fun execute() {
        /**
         * Raw GraphQL query
         */
        val query = "query ListWorkflowsGraphQL(\$datasetIds: [Int], \$workflowIds: [Int]){\n    workflows(datasetIds: \$datasetIds, workflowIds: \$workflowIds){\n        workflows {\n            id\n            name\n            }\n    }\n}"
        val operationName = "ListWorkflowsGraphQL"
        val json = "{ \"datasetIds\": [76663], \"workflowIds\": [18838] }"
        val objectMapper = ObjectMapper()
        val variables = objectMapper.readTree(json)
        val graphQLRequest = main.client.rawGraphQLQuery(query, operationName, variables)!!
        val result = graphQLRequest.query()
        assertNotNull(result)
    }
}