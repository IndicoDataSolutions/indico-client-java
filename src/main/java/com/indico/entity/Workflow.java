package com.indico.entity;

public class Workflow {

    public final int id;
    public final String name;
    public final boolean reviewEnabled;

    private Workflow(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.reviewEnabled = builder.reviewEnabled;
    }

    public static class Builder {

        protected int id;
        protected String name;
        protected boolean reviewEnabled;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder reviewEnabled(boolean reviewEnabled) {
            this.reviewEnabled = reviewEnabled;
            return this;
        }

        public Workflow build() {
            return new Workflow(this);
        }
    }
}
