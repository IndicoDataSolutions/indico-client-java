package com.indico.entity;

import com.indico.type.ModelStatus;

public class Model {

    public final int id;
    public final ModelStatus status;

    protected Model(int id, ModelStatus modelStatus) {
        this.id = id;
        this.status = modelStatus;
    }
}
