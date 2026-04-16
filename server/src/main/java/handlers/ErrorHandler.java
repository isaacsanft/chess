package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import result.ErrorResult;
import serializer.JsonSerializer;

public class ErrorHandler {

    protected static void handleException(DataAccessException exception, Context context) {
        if (exception.getMessage().contains("already taken")) {
            context.status(403);
        }
        else if (exception.getMessage().contains("unauthorized")) {
            context.status(401);
        }
        else if (exception.getMessage().contains("bad request")) {
            context.status(400);
        }
        else {
            context.status(500);
        }
        ErrorResult errorResult = new ErrorResult("Error: " + exception.getMessage());
        context.result(JsonSerializer.toJson(errorResult));
    }
}