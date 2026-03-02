package dataaccess;

import chess.ChessGame;
import model.Game;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{

    private final HashMap<Integer, Game> games = new HashMap<>();
    private int iter = 1;

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int gameID = iter;
        iter++;
        ChessGame chessGame = new ChessGame();
        Game game = new Game(gameID, null, null, gameName, chessGame);
        games.put(gameID, game);
        return gameID;
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

    @Override
    public Collection<Game> listGames() throws DataAccessException {
        return games.values();
    }
}
