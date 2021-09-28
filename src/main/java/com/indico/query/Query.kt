package com.indico.query

import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.indico.exceptions.IndicoMutationException
import com.indico.exceptions.IndicoQueryException
import java.lang.StringBuilder

abstract class Query<T,R> {
    /**
     * Execute the graphql query and retunrs the results as a specific type
     * @return result of query of type T
     */
    abstract fun query(): T
    @Deprecated("Not supported")
    abstract fun refresh(obj: T): T

    /**
     * Raises IndicoQueryError if there are errors.
     */
    protected fun handleErrors(response: GraphQLClientResponse<R>) {
        if (response.errors != null && response.errors!!.isNotEmpty()) {
            val errors = StringBuilder()
            for (err in response.errors!!.asIterable()) {
                errors.append(err.toString() + "\n")
            }
            throw IndicoQueryException(errors.toString())
        }
    }
}