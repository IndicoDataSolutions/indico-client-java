package com.indico

abstract class Mutation<T> {
    /**
     * Execute the graphql query and retunrs the results as a specific type
     *
     * @return result of query of type T
     */
    open fun execute(): T? {
        return null
    }
}