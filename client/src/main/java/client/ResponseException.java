package client;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    final private int code;

    public ResponseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static ResponseException fromJson(int code, String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        String message = (String) map.get("message");
        return new ResponseException(code, message);
    }
}
