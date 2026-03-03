package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.CreateRequest;
import result.CreateResult;
import result.ErrorResult;
import serializer.JsonSerializer;
import service.GameService;

public class CreateHandler {
    private final GameService game;

    public CreateHandler(GameService game) {
        this.game = game;
    }


    public void create(Context context) throws DataAccessException {
        try {
            String authToken = context.header("Authorization");
            String json = context.body();
            CreateRequest createRequest = JsonSerializer.fromJson(json, CreateRequest.class);
            CreateResult createResult = game.create(authToken, createRequest);

            context.status(200);
            context.result(JsonSerializer.toJson(createResult));
        }
        catch (DataAccessException exception) {
            if(exception.getMessage().contains("bad request")) {
                context.status(400);
            }
            else if (exception.getMessage().contains("unauthorized")) {
                context.status(401);
            }
            else {
                context.status(500);
            }
            ErrorResult errorResult = new ErrorResult("Error: " + exception.getMessage());
            context.result(JsonSerializer.toJson(errorResult));
        }
    }
}