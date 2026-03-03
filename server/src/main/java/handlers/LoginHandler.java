package handlers;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import request.LoginRequest;
import result.ErrorResult;
import result.LoginResult;
import serializer.JsonSerializer;
import service.UserService;

public class LoginHandler {
    private final UserService user;

    public LoginHandler(UserService user) {
        this.user = user;
    }

    public void login(Context context) throws DataAccessException {
        try {
            String json = context.body();
            LoginRequest loginRequest = JsonSerializer.fromJson(json, LoginRequest.class);
            LoginResult loginResult = user.login(loginRequest);

            context.status(200);
            context.result(JsonSerializer.toJson(loginResult));
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
