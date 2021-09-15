package com.indico.query

import com.indico.IndicoClient
import com.indico.Query
import com.indico.entity.Model
import com.indico.entity.TrainingProgress
import com.indico.exceptions.IndicoQueryException
import com.indico.graphql.ModelGroupProgressGraphQLQuery
import java.util.*

class TrainingModelWithProgressQuery(private val client: IndicoClient) : Query<Model?> {
    private var id = 0
    private var name: String? = null

    /**
     * Use to query TrainingModelWithProgress by id
     *
     * @param id
     * @return TrainingModelWithProgressQuery
     */
    fun id(id: Int): TrainingModelWithProgressQuery {
        this.id = id
        return this
    }

    /**
     * Use to query TrainingModelWithProgress by name
     *
     * @param name
     * @return TrainingModelWithProgressQuery
     */
    @Deprecated("not supported")
    fun name(name: String?): TrainingModelWithProgressQuery {
        this.name = name
        return this
    }

    /**
     * Queries the server and returns Training Model
     *
     * @return Model
     */
    override fun query(): Model {
        return try {
            val call = ModelGroupProgressGraphQLQuery(ModelGroupProgressGraphQLQuery.Variables(
                id = id
            ))
            val response = client.execute(call)
            val modelGroups = response.data!!.modelGroups!!.modelGroups!!
            if (modelGroups.isEmpty()) {
                throw IndicoQueryException("Cannot find Model Group $id")
            }
            val models = modelGroups[0]!!.models!!
            val model = models.maxByOrNull { m -> m!!.id!! }

            val progress = TrainingProgress(model!!.trainingProgress!!.percentComplete!!.toDouble())
            Model.Builder()
                .id(model!!.id!!)
                .status(model!!.status!!)
                .trainingProgress(progress)
                .build()
        }catch (ex: RuntimeException) {
            throw IndicoQueryException("Call to Train Model failed", ex)
        }
    }

    override fun refresh(obj: Model?): Model? {
        return obj
    }

}