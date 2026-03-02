package dataaccess;

import model.Game;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    private final HashMap<Integer, Game> games = new HashMap<>();

    @Override
    public void createGame(Game game) throws DataAccessException {
        Integer gameID = game.gameID();
        games.put(gameID, game);
    }

    @Override
    public Game getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public void updateGame(Game game) throws DataAccessException {
        Integer gameID = game.gameID();
        games.put(gameID, game);
    }

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }
}
