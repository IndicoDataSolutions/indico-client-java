package com.indico.query

import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.indico.IndicoClient
import com.indico.entity.Workflow
import com.indico.graphql.ListWorkflowsGraphQL
import java.util.ArrayList

class ListWorkflows(private val client: IndicoClient) : Query<List<Workflow>?, ListWorkflowsGraphQL.Result>() {
    private var datasetIds: List<Int>? = null
    private var workflowIds: List<Int>? = null

    /**
     * List of dataset ids to filter by
     * @param ids Dataset Ids
     * @return ListWorkflows
     */
    fun datasetIds(ids: List<Int>?): ListWorkflows {
        datasetIds = ids
        return this
    }

    /**
     * List of workflow ids to filter by
     * @param ids
     * @return ListWorkflows
     */
    fun workflowIds(ids: List<Int>?): ListWorkflows {
        workflowIds = ids
        return this
    }

    /**
     * Queries the server and returns Workflow List
     * @return Workflow List
     */
    override fun query(): List<Workflow> {
        return try {
            val call = ListWorkflowsGraphQL(ListWorkflowsGraphQL.Variables(
                datasetIds = if (datasetIds != null) OptionalInput.Defined(datasetIds) else OptionalInput.Undefined,
                workflowIds = if (workflowIds !=null) OptionalInput.Defined(workflowIds) else OptionalInput.Undefined

            ))
            val response = client.execute(call)
            handleErrors(response)
            val wf = response.data?.workflows?.workflows?: ArrayList()
            val workflows: MutableList<Workflow> = ArrayList()
            wf.forEach { workflow ->
                workflows.add(
                    Workflow.Builder()
                        .id(workflow!!.id!!)
                        .name(workflow.name!!)
                        .reviewEnabled(workflow.reviewEnabled!!)
                        .build()
                )
            }
            workflows
        } catch (ex: InterruptedException) {
            throw RuntimeException("Call to list workflows failed", ex)
        }
    }

    override fun refresh(obj: List<Workflow>?): List<Workflow>? {
        return obj
    }
}