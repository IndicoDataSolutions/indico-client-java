package com.indico.mutations

import com.indico.IndicoKtorClient
import com.indico.entity.ModelGroup
import com.indico.exceptions.IndicoMutationException
import com.indico.graphql.PredictModelGraphQL
import com.indico.query.Job

class ModelGroupPredict(private val indicoClient: IndicoKtorClient) : Mutation<Job?, PredictModelGraphQL.Result>() {
    private var modelId = 0
    private var data: List<String?> = emptyList()

    /**
     * Use to predict ModelGroup
     *
     * @param modelGroup ModelGroup
     * @return ModelGroupPredict
     */
    fun modelGroup(modelGroup: ModelGroup): ModelGroupPredict {
        modelId = modelGroup.selectedModel.id
        return this
    }

    /**
     * Use to predict ModelGroup by id
     *
     * @param modelId Model id
     * @return ModelGroupPredict
     */
    fun modelId(modelId: Int): ModelGroupPredict {
        this.modelId = modelId
        return this
    }

    /**
     * Data to predict
     *
     * @param data Data
     * @return ModelGroupPredict
     */
    fun data(data: List<String?>): ModelGroupPredict {
        this.data = data
        return this
    }

    /**
     * Executes request and returns job
     *
     * @return Job
     */
    override fun execute(): Job? {
        return try {
            val call = PredictModelGraphQL(
                variables = PredictModelGraphQL.Variables(
                    modelId = this.modelId,
                    data = this.data
                )
            )

            val response = indicoClient.execute(call)
            val jobId: String = response.data?.modelPredict?.jobId?:
                throw IndicoMutationException("Error retrieving job id for model group predict")
            Job(indicoClient, id = jobId, errors = emptyList())
        } catch (ex: RuntimeException) {
            throw IndicoMutationException("Call for Model Group Predict failed", ex)
        }
    }

}