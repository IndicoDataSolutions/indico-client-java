package com.indico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSON {
    private String jsonString;

    public JSON(String jsonString) {
        this.jsonString = jsonString;
    }

    public JSONObject asJSONObject() throws JSONException {
        try {
            return new JSONObject(this.jsonString);
        } catch (JSONException err) {
            throw new JSONException(this.jsonString + " is invalid JSON");
        }
    }

    public JSONArray asJSONArray() throws JSONException {
        try {
            return new JSONArray(this.jsonString);
        } catch (JSONException err) {
            throw new JSONException(this.jsonString + " is invalid JSON Array");
        }
    }
<<<<<<< HEAD
}
=======
}
>>>>>>> 704f32a... Added better exceptions for json parsing
