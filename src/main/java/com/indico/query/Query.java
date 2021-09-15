package com.indico.query;

public interface Query<T> {

    /**
     * Execute the graphql query and retunrs the results as a specific type
     * @return result of query of type T
     */
    public T query();
    public T refresh(T obj);

}