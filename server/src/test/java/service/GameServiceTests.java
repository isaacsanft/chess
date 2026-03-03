package service;

import chess.ChessGame;
import dataaccess.*;
import model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceTests {

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    final GameService game = new GameService(userDAO, authDAO, gameDAO);
    final UserService user = new UserService(userDAO, authDAO);

    @BeforeEach
    public void clear() throws DataAccessException {
        ClearService clear = new ClearService(userDAO, authDAO, gameDAO);
        clear.clear();
    }

    @Test
    public void createPositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        String token = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("Test Game", token);
        CreateResult createResult = game.create(token, createRequest);

        int gameID = createResult.gameID();
        assertTrue(gameID >= 1);
        assertNotNull(gameDAO.getGame(gameID));
    }

    @Test
    public void createNegative() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        String token = "incorrect";
        CreateRequest createRequest = new CreateRequest("Test Game", token);
        assertThrows(DataAccessException.class, () -> {
            game.create(token, createRequest);
        });
    }

    @Test
    public void joinPositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        String token = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("Test Game", token);
        CreateResult createResult = game.create(token, createRequest);
        int gameID = createResult.gameID();

        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.BLACK, gameID, token);
        JoinResult joinResult = game.join(token, joinRequest);

        Game updatedGame = gameDAO.getGame(gameID);
        assertNotNull(joinResult);
        assertEquals("Isaac", updatedGame.blackUsername());
    }

    @Test
    public void joinNegative() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        String token = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("Test Game", token);
        CreateResult createResult = game.create(token, createRequest);
        int gameID = 0;

        JoinRequest joinRequest = new JoinRequest(ChessGame.TeamColor.BLACK, gameID, token);
        assertThrows(DataAccessException.class, () -> {
            game.join(token, joinRequest);
        });
    }

    @Test
    public void listPositive() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        String token = registerResult.authToken();
        CreateRequest createRequest = new CreateRequest("Test Game", token);
        CreateResult createResult = game.create(token, createRequest);

        ListRequest listRequest = new ListRequest(token);
        ListResult listResult = game.list(token, listRequest);

        Collection<Game> games = listResult.games();
        assertTrue(!games.isEmpty());
    }

    @Test
    public void listNegative() throws DataAccessException {
        RegisterRequest registerRequest = new RegisterRequest("Isaac", "mypassword", "isaac@email.com");
        RegisterResult registerResult = user.register(registerRequest);

        String realToken = registerResult.authToken();
        String fakeToken = "incorrect";
        CreateRequest createRequest = new CreateRequest("Test Game", realToken);
        CreateResult createResult = game.create(realToken, createRequest);

        ListRequest listRequest = new ListRequest(fakeToken);
        assertThrows(DataAccessException.class, () -> {
            game.list(fakeToken, listRequest);
        });
    }
}