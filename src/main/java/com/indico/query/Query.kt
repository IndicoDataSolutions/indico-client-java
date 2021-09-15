package com.indico.query

sealed interface Query<T> {
    /**
     * Execute the graphql query and retunrs the results as a specific type
     * @return result of query of type T
     */
    fun query(): T
    @Deprecated("Not supported")
    fun refresh(obj: T): T
}