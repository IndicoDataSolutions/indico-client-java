package com.indico.mutations


import com.indico.IndicoKtorClient
import com.indico.entity.ModelGroup
import com.indico.exceptions.IndicoMutationException
import com.indico.graphql.LoadModelGraphQL

class ModelGroupLoad(private val indicoClient: IndicoKtorClient) : Mutation<String?, LoadModelGraphQL.Result>() {
    private var modelId = 0

    /**
     * Use to load ModelGroup
     *
     * @param modelGroup ModelGroup
     * @return ModelGroupLoad
     */
    fun modelGroup(modelGroup: ModelGroup): ModelGroupLoad {
        modelId = modelGroup.selectedModel.id
        return this
    }

    /**
     * Use to load ModelGroup by id
     *
     * @param modelId Model id
     * @return ModelGroupLoad
     */
    fun modelId(modelId: Int): ModelGroupLoad {
        this.modelId = modelId
        return this
    }

    /**
     * Executes request and returns load status
     *
     * @return Load status
     */
    override fun execute(): String? {
        return try {
            val call  = LoadModelGraphQL(variables = LoadModelGraphQL.Variables(
                model_id = this.modelId
            ))
            val response  = indicoClient.execute(call)
            response.data?.modelLoad?.status?:
                throw IndicoMutationException("Could not fetch status for $modelId")
        } catch (ex: RuntimeException) {
            throw IndicoMutationException("Call to load model group failed", ex)
        }
    }
}