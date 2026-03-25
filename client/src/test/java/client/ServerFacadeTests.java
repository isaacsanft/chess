package client;

import model.AuthToken;
import org.junit.jupiter.api.*;
import request.CreateRequest;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.CreateResult;
import result.LoginResult;
import result.RegisterResult;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clear() throws ResponseException {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        assertTrue(true);
    }

    @Test
    public void registerPositive() throws ResponseException {
        RegisterRequest request = new RegisterRequest("Isaac", "password", "isaac@email.com");
        RegisterResult result = facade.register(request);
        assertEquals(result.username(), "Isaac");
        assertNotNull(result.authToken());
    }

    @Test
    public void registerNegative() throws ResponseException {
        RegisterRequest request1 = new RegisterRequest("Isaac", "password", "isaac@email.com");
        RegisterResult result = facade.register(request1);
        RegisterRequest request2 = new RegisterRequest("Isaac", "password", "isaac@email.com");
        Assertions.assertThrows(ResponseException.class, () -> {
            facade.register(request2);
        });
    }

    @Test
    public void loginPositive() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "password", "isaac@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Isaac", "password");
        LoginResult loginResult = facade.login(loginRequest);
        assertEquals(loginResult.username(), "Isaac");
        assertNotNull(loginResult.authToken());
    }

    @Test
    public void loginNegative() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "password", "isaac@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        LoginRequest loginRequest = new LoginRequest("Isaac", "wrong");
        assertThrows(ResponseException.class, () ->
                facade.login(loginRequest));
    }

    @Test
    public void logoutPositive() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "password", "isaac@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        String authToken = registerResult.authToken();
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        assertDoesNotThrow(() ->
                facade.logout(logoutRequest));
    }

    @Test
    public void logoutNegative() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "password", "isaac@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        String authToken = "fake";
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        assertThrows(ResponseException.class, () ->
                facade.logout(logoutRequest));
    }

    @Test
    public void createPositive() throws ResponseException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "password", "isaac@email.com");
        RegisterResult registerResult = facade.register(registerRequest);
        String authToken = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("test", authToken);
        CreateResult createResult = facade.create(createRequest);
        assertNotNull(createResult.gameID());
    }

    @Test
    public void createNegative() throws ResponseException {
        String authToken = "fake";
        CreateRequest createRequest = new CreateRequest("test", authToken);
        assertThrows(ResponseException.class, () ->
                facade.create(createRequest));
    }


}
