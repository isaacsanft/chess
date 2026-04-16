package handlers;


import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.JoinRequest;
import result.ErrorResult;
import result.JoinResult;
import serializer.JsonSerializer;
import service.GameService;

public class JoinHandler {
    private final GameService game;

    public JoinHandler(GameService game) {
        this.game = game;
    }


    public void join(Context context) throws DataAccessException {
        try {
            String authToken = context.header("Authorization");
            String json = context.body();
            JoinRequest joinRequest = JsonSerializer.fromJson(json, JoinRequest.class);
            JoinResult joinResult = game.join(authToken, joinRequest);

            context.status(200);
            context.result(JsonSerializer.toJson(joinResult));
        }
        catch (DataAccessException exception) {
            ErrorHandler.handleException(exception, context);
        }
    }
}
