package dataaccess;

import model.Game;

public interface GameDAO {
    void createGame(Game game) throws DataAccessException;
    Game getGame(int gameID) throws DataAccessException;
    void updateGame(Game game) throws DataAccessException;
    void clear() throws DataAccessException;
}
