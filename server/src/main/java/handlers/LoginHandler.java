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
            ErrorHandler.handleException(exception, context);
        }
    }
}
