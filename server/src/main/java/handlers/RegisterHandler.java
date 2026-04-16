package handlers;

import dataaccess.*;
import request.RegisterRequest;
import result.ErrorResult;
import result.RegisterResult;
import serializer.JsonSerializer;
import service.UserService;

import io.javalin.http.Context;


public class RegisterHandler {
    private final UserService user;

    public RegisterHandler(UserService user) {
        this.user = user;
    }

    public void register(Context context) throws DataAccessException {
        try {
            String json = context.body();
            RegisterRequest registerRequest = JsonSerializer.fromJson(json, RegisterRequest.class);
            RegisterResult registerResult = user.register(registerRequest);

            context.status(200);
            context.result(JsonSerializer.toJson(registerResult));
        }
        catch (DataAccessException exception) {
            ErrorHandler.handleException(exception, context);
        }
    }
}
