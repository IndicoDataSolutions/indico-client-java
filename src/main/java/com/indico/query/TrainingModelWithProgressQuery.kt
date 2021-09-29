package com.indico.query

import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.indico.IndicoClient
import com.indico.entity.Model
import com.indico.entity.TrainingProgress
import com.indico.exceptions.IndicoQueryException
import com.indico.graphql.ModelGroupProgressGraphQLQuery
import java.util.*

class TrainingModelWithProgressQuery(private val client: IndicoClient) :
    Query<Model?, ModelGroupProgressGraphQLQuery.Result>() {
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
                id = if(id != null) OptionalInput.Defined(id) else OptionalInput.Undefined
            ))
            val response = client.execute(call)
            handleErrors(response)
            val modelGroups = response.data?.modelGroups?.modelGroups?:
            throw IndicoQueryException("Error fetching model group for model $id")
            if (modelGroups.isEmpty()) {
                throw IndicoQueryException("Cannot find Model Group $id")
            }
            val models = modelGroups[0]?.models?: throw IndicoQueryException("Error fetching models for model group $id")
            val model = models.maxByOrNull { m -> m!!.id!! }

            val percentComplete = model?.trainingProgress?.percentComplete?:
            throw IndicoQueryException("Model progress percent is missing for $id")
            val progress = TrainingProgress(percentComplete.toDouble())
            Model.Builder()
                .id(model.id!!)
                .status(model.status!!)
                .trainingProgress(progress)
                .build()
        }catch (ex: RuntimeException) {
            throw IndicoQueryException("Call to check Model progress failed", ex)
        }
    }

    override fun refresh(obj: Model?): Model? {
        return obj
    }

}