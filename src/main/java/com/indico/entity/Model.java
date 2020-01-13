package com.indico.entity;

import org.json.JSONObject;

public class Model {

    public final int id;
    public final JSONObject modelInfo;

    protected Model(int id, Object modelInfo) {
        this.id = id;
        this.modelInfo = new JSONObject(modelInfo.toString());
    }
}
