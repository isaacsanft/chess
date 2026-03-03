package handlers;

import dataaccess.DataAccessException;
import result.ErrorResult;
import serializer.JsonSerializer;
import service.ClearService;
import io.javalin.http.Context;

import java.util.Map;

public class ClearHandler {
    private final ClearService clear;

    public ClearHandler(ClearService clear) {
        this.clear = clear;
    }

    public void clear(Context context) {
        try {
            clear.clear();
            context.status(200);
            context.result("{}");
        }
        catch (DataAccessException exception) {
            context.status(500);
            ErrorResult errorResult = new ErrorResult("Error: " + exception.getMessage());
            context.result(JsonSerializer.toJson(errorResult));
        }
    }
}
