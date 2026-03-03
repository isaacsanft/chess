package service;

import dataaccess.*;
import model.AuthToken;
import model.Game;
import model.User;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClearServiceTests {
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();

    @Test
    public void clearPositive() throws DataAccessException {
        User user = new User("Isaac", "mypassword", "isaac@email.com");
        AuthToken authToken = new AuthToken("authtoken", "Isaac");
        userDAO.createUser(user);
        authDAO.createAuth(authToken);
        gameDAO.createGame("Test");

        ClearService clear = new ClearService(userDAO, authDAO, gameDAO);
        clear.clear();

        Collection<Game> games = gameDAO.listGames();
        assertNull(userDAO.getUser("Isaac"));
        assertNull(authDAO.getAuth("authtoken"));
        assertTrue(games.isEmpty());
    }
}
