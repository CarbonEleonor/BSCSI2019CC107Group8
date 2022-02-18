package com.omnicron.avax.helpers;

import com.android.volley.NetworkResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ErrorMessage {
    private int code = 0;
    private String message = "";

    public ErrorMessage(NetworkResponse error){
        JSONObject data = null;
        try {
            data = new JSONObject(new String(error.data, "UTF-8"));
            this.message = data.getString("error");
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        code = error.statusCode;

    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
