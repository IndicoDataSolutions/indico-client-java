package com.indico.jobs;

import java.util.List;

import com.indico.Query;


public class JobQuery implements Query<Job> {
    private String id;

    public JobQuery id(String id) {
        this.id = id;

        return this;
    }

    public Job query() {
        return new Job();
    }

    public Job refresh(Job obj) {
        return obj;
    }
}