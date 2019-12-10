package com.indico; 

public interface Mutation<T> {

    /**
     * Execute the graphql query and retunrs the results as a specific type
     * @return result of query of type T
     */
    public T execute();
}