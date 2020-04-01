package com.indico.entity;

import com.indico.ModelGroupGraphQLQuery;
import com.indico.type.ModelStatus;

public class ModelGroup {

    public final int id;
    public final String name;
    public final ModelStatus status;
    public final Model selectedModel;

    public ModelGroup(ModelGroupGraphQLQuery.ModelGroup modelGroup) {
        this.id = modelGroup.id();
        this.name = modelGroup.name();
        this.status = modelGroup.status();
        this.selectedModel = new Model(modelGroup.selectedModel().id(), modelGroup.selectedModel().status());
    }
}
