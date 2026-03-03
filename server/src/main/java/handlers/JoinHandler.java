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
            if(exception.getMessage().contains("bad request")) {
                context.status(400);
            }
            else if (exception.getMessage().contains("unauthorized")) {
                context.status(401);
            }
            else if (exception.getMessage().contains("already taken")) {
                context.status(403);
            }
            else {
                context.status(500);
            }
            ErrorResult errorResult = new ErrorResult("Error: " + exception.getMessage());
            context.result(JsonSerializer.toJson(errorResult));
        }
    }
}
