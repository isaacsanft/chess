package servicetests;

import dataaccess.*;
import model.AuthToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.ClearService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    final UserService user = new UserService(userDAO, authDAO);

    @BeforeEach
    public void clear() throws DataAccessException {
        ClearService clear = new ClearService(userDAO, authDAO, gameDAO);
        clear.clear();
    }

    @Test
    public void registerPositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        assertEquals("Isaac", registerResult.username());
        assertNotNull(registerResult.authToken());
    }

    @Test
    public void registerNegative() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", null, "isaac@email.com");

        assertThrows(DataAccessException.class, () -> {user.register(registerRequest);});
    }

    @Test
    public void loginPositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("Isaac", "mypassword");
        LoginResult loginResult = user.login(loginRequest);

        assertEquals("Isaac", loginResult.username());
        assertNotNull(loginResult.authToken());
    }

    @Test
    public void loginNegative() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("Isaac", null);

        assertThrows(DataAccessException.class, () -> {user.login(loginRequest);});
    }

    @Test
    public void logoutPositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("Isaac", "mypassword");
        LoginResult loginResult = user.login(loginRequest);

        String authToken = loginResult.authToken();

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        user.logout(logoutRequest);

        AuthToken nullToken = authDAO.getAuth(authToken);
        assertNull(nullToken);
    }

    @Test
    public void logoutNegative() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("Isaac", "mypassword");
        LoginResult loginResult = user.login(loginRequest);

        String authToken = "incorrect";

        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        assertThrows(DataAccessException.class, () -> {user.logout(logoutRequest);});
    }
}