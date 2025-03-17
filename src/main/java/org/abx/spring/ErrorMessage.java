package org.abx.spring;

import org.json.JSONObject;

public class ErrorMessage {
    public static JSONObject error(String message) {
        JSONObject obj = new JSONObject();
        obj.put("error", true);
        obj.put("message", message);
        return obj;
    }

    public static String errorString(String message) {
        return error(message).toString();
    }
}
