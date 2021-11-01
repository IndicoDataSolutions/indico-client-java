package com.indico.entity;

import com.indico.graphql.enums.ModelStatus;

public class ModelGroup {

    public final int id;
    public final String name;
    public final ModelStatus status;
    public final Model selectedModel;

    private ModelGroup(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.status = builder.status;
        this.selectedModel = builder.selectedModel;
    }

    public static class Builder {

        protected int id;
        protected String name;
        protected ModelStatus status;
        protected Model selectedModel;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder status(ModelStatus status) {
            this.status = status;
            return this;
        }

        public Builder selectedModel(Model model) {
            this.selectedModel = model;
            return this;
        }

        public ModelGroup build() {
            return new ModelGroup(this);
        }
    }
}
