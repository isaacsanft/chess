package handlers;

import dataaccess.DataAccessException;
import request.LoginRequest;
import request.LogoutRequest;
import result.ErrorResult;
import result.LoginResult;
import result.LogoutResult;
import serializer.JsonSerializer;
import io.javalin.http.Context;
import service.UserService;

public class LogoutHandler {
    private final UserService user;

    public LogoutHandler(UserService user) {
        this.user = user;
    }

    public void logout(Context context) throws DataAccessException {
        try {
            String authToken = context.header("Authorization");
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            user.logout(logoutRequest);

            context.status(200);
            context.result("{}");
        }
        catch (DataAccessException exception) {
            context.status(401);
            ErrorResult errorResult = new ErrorResult("Error: " + exception.getMessage());
            context.result(JsonSerializer.toJson(errorResult));
        }
    }
}
