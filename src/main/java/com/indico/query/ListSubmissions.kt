package com.indico.query

import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.indico.IndicoKtorClient
import com.indico.entity.SubmissionFilter
import com.indico.graphql.ListSubmissionsGraphQL
import com.indico.entity.Submission
import com.indico.exceptions.IndicoQueryException
import com.indico.graphql.enums.SubmissionStatus
import com.sun.corba.se.impl.orbutil.graph.Graph
import java.util.*
import java.util.function.Consumer
import com.indico.graphql.inputs.SubmissionFilter as GraphQlSubmissionFilter

class ListSubmissions(private val client: IndicoKtorClient) :
    Query<List<Submission?>?, ListSubmissionsGraphQL.Result>() {
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

    private fun convertSingleFilter(filter: SubmissionFilter, ands: List<GraphQlSubmissionFilter>? = ArrayList(), ors: List<GraphQlSubmissionFilter>? = ArrayList()): GraphQlSubmissionFilter{
        var retrieved: OptionalInput<Boolean> = OptionalInput.Undefined
        var status: OptionalInput<SubmissionStatus> = OptionalInput.Undefined
        var inputFileName : OptionalInput<String> = OptionalInput.Undefined
        if(filter.retrieved != null){
            retrieved = OptionalInput.Defined(filter.retrieved)
        }
        if(filter.status != null){
            status = OptionalInput.Defined(filter.status)
        }
        if(filter.inputFileName != null){
            inputFileName = OptionalInput.Defined(filter.inputFileName)
        }
        var optionalAnds: OptionalInput<List<GraphQlSubmissionFilter>> = OptionalInput.Undefined
        var optionalOrs : OptionalInput<List<GraphQlSubmissionFilter>> = OptionalInput.Undefined
        if(ands?.isNotEmpty() == true){
            optionalAnds = OptionalInput.Defined(ands)
        }
        if(ors?.isNotEmpty() == true){
            optionalOrs = OptionalInput.Defined(ors)
        }
        return  GraphQlSubmissionFilter(
            retrieved = retrieved,
            status = status, inputFilename = inputFileName, ands = optionalAnds, ors = optionalOrs

        )
    }
    private fun convertFilterList(filters: List<SubmissionFilter>?): ArrayList<GraphQlSubmissionFilter> {
        val filterlist = ArrayList<GraphQlSubmissionFilter>()
        filters?.iterator()?.forEach {
            filterlist.add(
                convertSingleFilter(it)
            )
        }
        return filterlist
    }
    private fun convertFilters(): OptionalInput<GraphQlSubmissionFilter> {
        val ands = convertFilterList(filters?.ands as List<SubmissionFilter>?)

        val ors = convertFilterList(filters?.ors as List<SubmissionFilter>?)
        val converted = filters?.let { convertSingleFilter(it , ands=ands, ors=ors) }

        return if(converted != null) OptionalInput.Defined(converted) else OptionalInput.Undefined
    }

    /**
     * Execute the query
     * @return Submission List
     */
    override fun query(): List<Submission> {
        return try {
            val variables = ListSubmissionsGraphQL.Variables(
                submissionIds = if (submissionIds != null) OptionalInput.Defined(submissionIds) else OptionalInput.Undefined,
                workflowIds = if (workflowIds?.any() == true) OptionalInput.Defined(workflowIds) else OptionalInput.Undefined,
                convertFilters(), OptionalInput.Defined(limit))
            val listSubmissionsGraphQL = ListSubmissionsGraphQL(variables)
            val result = client.execute(listSubmissionsGraphQL)
            handleErrors(result)
            val submissionList = result.data?.submissions?.submissions?: ArrayList()
            val submissions = ArrayList<Submission>()
            submissionList.forEach(Consumer { submission: com.indico.graphql.listsubmissionsgraphql.Submission? ->
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
        } catch (ex: RuntimeException) {
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