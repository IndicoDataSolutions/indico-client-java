package com.indico.workflows;

import com.indico.ModelGroupGraphQLQuery;
import com.indico.type.ModelStatus;
import org.json.JSONObject;

public class ModelGroup {

    public final int id;
    public final String name;
    public final ModelStatus status;
    public final SelectedModel selectedModel;

    public class SelectedModel {

        public final int id;
        public final JSONObject modelInfo;

        protected SelectedModel(ModelGroupGraphQLQuery.SelectedModel selectedModel) {
            this.id = selectedModel.id();
            this.modelInfo = new JSONObject(selectedModel.modelInfo().toString());
        }
    }

    protected ModelGroup(ModelGroupGraphQLQuery.ModelGroup modelGroup) {
        this.id = modelGroup.id();
        this.name = modelGroup.name();
        this.status = modelGroup.status();
        this.selectedModel = new SelectedModel(modelGroup.selectedModel());
    }
}
