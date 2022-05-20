package com.indico.requests

import com.indico.IndicoClient
import com.indico.IndicoHelper
import com.indico.type.JobStatus
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
        val rawGraphQL = client.rawGraphQLQuery()
        Assertions.assertNotNull(rawGraphQL)

    }
}