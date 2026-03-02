package dataaccess;

import model.Game;

import java.util.Collection;

public interface GameDAO {
    int createGame(String gameName) throws DataAccessException;
    Game getGame(int gameID) throws DataAccessException;
    void updateGame(Game game) throws DataAccessException;
    void clear() throws DataAccessException;
    Collection<Game> listGames();
}
