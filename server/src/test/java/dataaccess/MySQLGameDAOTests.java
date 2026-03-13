package dataaccess;

import model.Game;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLGameDAOTests {

    private static GameDAO gameDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        DatabaseManager.configureDatabase();
        gameDAO = new MySQLGameDAO();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO.clear();
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        int id = gameDAO.createGame("Test");
        assertTrue(id > 0);
    }

    @Test
    public void getGamePositive() throws DataAccessException {
        int id = gameDAO.createGame("Test");
        Game game = gameDAO.getGame(id);
        assertNotNull(game);
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        int id = gameDAO.createGame("Test");
        Game game = gameDAO.getGame(id);
        Game updatedGame = new Game(id, null, null, "New Name Test", game.game());
        gameDAO.updateGame(updatedGame);
        Game newGame = gameDAO.getGame(id);
        assertEquals("New Name Test", newGame.gameName());
    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        int id1 = gameDAO.createGame("Test");
        int id2 = gameDAO.createGame("Test");
        Collection<Game> games = gameDAO.listGames();
        assertTrue(games.size() == 2);
    }

    @Test
    public void createGameNegative() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameDAO.createGame(null));
    }

    @Test
    public void getGameNegative() throws DataAccessException {
        int id = gameDAO.createGame("Test");
        int fakeID = -1;
        Game game = gameDAO.getGame(fakeID);
        assertNull(game);
    }

    @Test
    public void updateGameNegative() throws DataAccessException {
        int id = gameDAO.createGame("Real Game");
        Game fakeGame = new Game(-1, "white", "black", "Fake Game", null);
        gameDAO.updateGame(fakeGame);
        assertEquals("Real Game", gameDAO.getGame(id).gameName());
        assertNull(gameDAO.getGame(-1));
    }

    @Test
    public void listGamesNegative() throws DataAccessException {
        Collection<Game> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }

    @Test
    public void clearPositive() throws DataAccessException {
        gameDAO.createGame("Game 1");
        gameDAO.createGame("Game 2");
        gameDAO.clear();
        Collection<Game> games = gameDAO.listGames();
        assertTrue(games.isEmpty());
    }
}
