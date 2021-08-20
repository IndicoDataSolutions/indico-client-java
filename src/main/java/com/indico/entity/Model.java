package com.indico.entity;

import com.indico.graphql.enums.ModelStatus;

public class Model {

    public final int id;
    public final ModelStatus status;
    public final TrainingProgress trainingProgress;

    private Model(Builder builder) {
        this.id = builder.id;
        this.status = builder.status;
        this.trainingProgress = builder.trainingProgress;
    }

    public static class Builder {

        protected int id;
        protected ModelStatus status;
        protected TrainingProgress trainingProgress;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder status(ModelStatus status) {
            this.status = status;
            return this;
        }

        public Builder trainingProgress(TrainingProgress trainingProgress) {
            this.trainingProgress = trainingProgress;
            return this;
        }

        public Model build() {
            return new Model(this);
        }
    }
}
