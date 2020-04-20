package com.indico.entity;

public class Workflow {

    public final int id;
    public final String name;

    private Workflow(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
    }

    public static class Builder {

        protected int id;
        protected String name;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Workflow build() {
            return new Workflow(this);
        }
    }
}
