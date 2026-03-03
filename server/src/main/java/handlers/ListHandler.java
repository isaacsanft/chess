package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.JoinRequest;
import request.ListRequest;
import result.ErrorResult;
import result.JoinResult;
import result.ListResult;
import serializer.JsonSerializer;
import service.GameService;

public class ListHandler {
    private final GameService game;

    public ListHandler(GameService game) {
        this.game = game;
    }


    public void list(Context context) throws DataAccessException {
        try {
            String authToken = context.header("Authorization");
            String json = context.body();
            ListRequest listRequest = JsonSerializer.fromJson(json, ListRequest.class);
            ListResult listResult = game.list(authToken, listRequest);

            context.status(200);
            context.result(JsonSerializer.toJson(listResult));
        }
        catch (DataAccessException exception) {
            context.status(401);
            ErrorResult errorResult = new ErrorResult("Error: " + exception.getMessage());
            context.result(JsonSerializer.toJson(errorResult));
        }
    }
}
