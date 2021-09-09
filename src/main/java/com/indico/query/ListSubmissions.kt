package com.indico.query

import com.indico.IndicoKtorClient
import com.indico.graphql.inputs.SubmissionFilter
import com.indico.query.ListSubmissions
import com.indico.graphql.ListSubmissionsGraphQL
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.indico.Query
import com.indico.entity.Submission
import java.util.concurrent.CompletionException
import com.indico.exceptions.IndicoQueryException
import java.util.ArrayList
import java.util.function.Consumer

class ListSubmissions(private val client: IndicoKtorClient) : Query<List<Submission?>?> {
    private var submissionIds: List<Int?>? = null
    private var workflowIds: List<Int?>? = null
    private var filters: SubmissionFilter? = null
    private var limit = 1000

    /**
     * Submission ids to filter by
     * @param ids submissionIds
     * @return ListSubmissions
     */
    fun submissionIds(ids: List<Int?>?): ListSubmissions {
        submissionIds = ids
        return this
    }

    /**
     * Workflow ids to filter by
     * @param ids workflowIds
     * @return ListSubmissions
     */
    fun workflowIds(ids: List<Int?>?): ListSubmissions {
        workflowIds = ids
        return this
    }

    /**
     * Submission attributes to filter by
     * @param filters submission filter
     * @return ListSubmissions
     */
    fun filters(filters: SubmissionFilter?): ListSubmissions {
        this.filters = filters
        return this
    }

    /**
     * Maximum number of Submissions to return. Defaults to 1000
     * @param limit
     * @return ListSubmissions
     */
    fun limit(limit: Int): ListSubmissions {
        this.limit = limit
        return this
    }

    /**
     * Execute the query
     * @return Submission List
     */
    override fun query(): List<Submission> {
        return try {
            val variables = ListSubmissionsGraphQL.Variables(submissionIds, workflowIds, filters, limit)
            val listSubmissionsGraphQL = ListSubmissionsGraphQL(variables)
            val result = client.execute(listSubmissionsGraphQL)
            val submissionList = result.data!!.submissions!!.submissions
            val submissions = ArrayList<Submission>()
            submissionList!!.forEach(Consumer { submission: com.indico.graphql.listsubmissionsgraphql.Submission? ->
                submissions.add(
                    Submission.Builder()
                        .id(submission!!.id!!)
                        .datasetId(submission.datasetId!!)
                        .workflowId(submission.workflowId!!)
                        .status(submission.status)
                        .inputFile(submission.inputFile)
                        .inputFilename(submission.inputFilename)
                        .resultFile(submission.resultFile)
                        .build()
                )
            })
            submissions
        } catch (ex: CompletionException) {
            throw IndicoQueryException("Call to list the submissions failed", ex)
        }
    }

    /**
     * Refresh the Submission List
     * @param obj
     * @return Submission List
     */
    override fun refresh(obj: List<Submission?>?): List<Submission?>? {
        return obj
    }
}