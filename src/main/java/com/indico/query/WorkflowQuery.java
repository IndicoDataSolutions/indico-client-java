package com.indico.query;

import com.indico.entity.Workflow;
import java.util.List;
import com.indico.Query;


public class WorkflowQuery implements Query<Workflow> {
    private int id;
    private String name;

    /**
     * Use to query workflow by id
     * @param id
     * @return WorkflowQuery
     */
    public WorkflowQuery id(int id) {
        this.id = id;

        return this;
    }

    /**
     * Use to query workflow by name
     * @param name
     * @return WorkflowQuery
     */
    public WorkflowQuery name(String name) {
        this.name = name;
        
        return this;
    }

    public Workflow query() {
        return new Workflow.Builder().build();
    }

    public Workflow refresh(Workflow obj) {
        return obj;
    }
}