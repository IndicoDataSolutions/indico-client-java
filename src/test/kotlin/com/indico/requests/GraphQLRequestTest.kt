package com.indico.requests

import com.indico.IndicoClient
import com.indico.IndicoHelper
import org.junit.jupiter.api.*

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
        val query = "query GetSubmissionGraphQL(\${'$'}submissionId: Int!){\n    submission(id: \${'$'}submissionId){\n        id\n        datasetId\n        workflowId\n        status\n        inputFile\n        inputFilename\n        resultFile\n        retrieved\n    }\n}"
        val operationName = "GetSubmissionGraphQL"
        val variables = ArrayList<String>()
        variables.add("0")
        val rawGraphQL = client.rawGraphQLQuery(query, operationName, variables)
        Assertions.assertNotNull(rawGraphQL)

    }
}