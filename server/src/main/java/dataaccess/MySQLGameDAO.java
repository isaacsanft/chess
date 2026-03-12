package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthToken;
import model.Game;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MySQLGameDAO implements GameDAO {
    @Override
    public int createGame(String gameName) throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        String json = new Gson().toJson(chessGame);
        var query = "INSERT INTO game (gameName, json) VALUES (?, ?)";
        int gameID = DatabaseManager.executeUpdate(query, gameName, json);
        return gameID;
    }

    @Override
    public Game getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var query = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM game WHERE gameID=?";
            try (var ps = conn.prepareStatement(query)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return jsonToGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void updateGame(Game game) throws DataAccessException {
        String json = new Gson().toJson(game.game());
        var query = "UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, json=? WHERE gameID=?";
        DatabaseManager.executeUpdate(query, game.whiteUsername(), game.blackUsername(), game.gameName(), json, game.gameID());
    }

    @Override
    public void clear() throws DataAccessException {
        var query = "TRUNCATE game";
        DatabaseManager.executeUpdate(query);
    }

    @Override
    public Collection<Game> listGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            Collection<Game> games = new ArrayList<>();
            var query = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM game";
            try (var ps = conn.prepareStatement(query)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Game game = jsonToGame(rs);
                        games.add(game);
                    }
                }
            }
            return games;
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
    }

    private Game jsonToGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        var blackUsername = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var json = rs.getString("json");
        var game = new Gson().fromJson(json, ChessGame.class);

        return new Game(gameID, whiteUsername, blackUsername, gameName, game);
    }
}
