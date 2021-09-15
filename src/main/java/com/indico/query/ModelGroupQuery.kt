package com.indico.query

import com.indico.IndicoClient
import com.indico.entity.Model
import com.indico.entity.ModelGroup
import com.indico.exceptions.IndicoQueryException
import com.indico.graphql.ModelGroupGraphQL
import java.util.ArrayList

class ModelGroupQuery(private val indicoClient: IndicoClient) : Query<ModelGroup?> {
    private var id = 0
    private var name: String? = null

    /**
     * Used to query ModelGroup by id
     *
     * @param id
     * @return ModelGroupQuery
     */
    fun id(id: Int): ModelGroupQuery {
        this.id = id
        return this
    }

    /**
     * Used to query ModelGroup by name
     *
     * @param name
     * @return ModelGroupQuery
     */
    fun name(name: String?): ModelGroupQuery {
        this.name = name
        return this
    }

    /**
     * Queries the server and returns ModelGroup
     *
     * @return ModelGroup
     */
    override fun query(): ModelGroup {
        return try {
            val modelGroupIds = ArrayList<Int>()
            modelGroupIds.add(id)
            val call = ModelGroupGraphQL(ModelGroupGraphQL.Variables(
                modelGroupIds = modelGroupIds
            ))
            val response = indicoClient.execute(call)
            val mg = response.data!!.modelGroups!!.modelGroups!![0]!!
            val model = Model.Builder()
                .id(mg.selectedModel!!.id!!)
                .status(mg.selectedModel.status!!)
                .build()
            ModelGroup.Builder()
                .id(mg.id!!)
                .name(mg.name!!)
                .status(mg.status!!)
                .selectedModel(model)
                .build()
        } catch (ex: RuntimeException) {
            throw IndicoQueryException("Call to generate the submission result failed", ex)
        }
    }

    /**
     * Refreshes the ModelGroup Object
     *
     * @param obj ModelGroup
     * @return ModelGroup
     */
    override fun refresh(obj: ModelGroup?): ModelGroup? {
        return obj
    }

}