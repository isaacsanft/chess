package service;

import dataaccess.*;
import dataaccess.DataAccessException;
import model.AuthToken;
import model.User;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();

        if (username == null || password == null || email == null) {
            throw new DataAccessException("Error: bad request");
        }

        if (userDAO.getUser(username) != null) {
            throw new DataAccessException("Error: already taken");
        }

        User newUser = new User(username, password, email);
        userDAO.createUser(newUser);
        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResult loginResult = login(loginRequest);
        String authToken = loginResult.authToken();
        RegisterResult registerResult = new RegisterResult(username, authToken);
        return registerResult;
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();

        if (username == null || password == null) {
            throw new DataAccessException("Error: bad request");
        }

        User user = userDAO.getUser(username);
        if (user == null || !user.password().equals(password)) {
            throw new DataAccessException("Error: unauthorized");
        }

        String token = UUID.randomUUID().toString();
        AuthToken authToken = new AuthToken(token, username);
        authDAO.createAuth(authToken);
        LoginResult loginResult = new LoginResult(username, token);
        return loginResult;
    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        String authToken = logoutRequest.authToken();
        if (authDAO.getAuth(authToken) == null) {
            throw new DataAccessException("Error: unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
}