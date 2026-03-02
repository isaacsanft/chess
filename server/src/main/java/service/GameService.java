package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthToken;
import model.Game;
import request.CreateRequest;
import request.JoinRequest;
import request.ListRequest;
import result.CreateResult;
import result.JoinResult;
import result.ListResult;

import java.util.Collection;

public class GameService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public CreateResult create(CreateRequest createRequest) throws DataAccessException {
        String gameName = createRequest.gameName();
        String token = createRequest.authToken();

        if (gameName == null) {
            throw new DataAccessException("Error: bad request");
        }

        AuthToken authToken = authDAO.getAuth(token);
        if (authToken == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        int gameID = gameDAO.createGame(gameName);
        CreateResult createResult = new CreateResult(gameID);
        return createResult;
    }

    public JoinResult join(JoinRequest joinRequest) throws DataAccessException {
        int gameID = joinRequest.gameID();
        ChessGame.TeamColor teamColor = joinRequest.playerColor();
        String token = joinRequest.authToken();

        AuthToken authToken = authDAO.getAuth(token);
        if (authToken == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        Game game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String username = authToken.username();
        if (username == null) {
            throw new DataAccessException("Error: bad request");
        }


        String blackUsername = game.blackUsername();
        String whiteUsername = game.whiteUsername();
        ChessGame chessGame = game.game();
        String gameName = game.gameName();
        if (teamColor == ChessGame.TeamColor.BLACK) {
            if (blackUsername != null) {
                throw new DataAccessException("Error: already taken");
            }
            Game updatedGame = new Game(gameID, whiteUsername, username, gameName, chessGame);
            gameDAO.updateGame(updatedGame);
        }
        else if (teamColor == ChessGame.TeamColor.WHITE) {
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
            Game updatedGame = new Game(gameID, username, blackUsername, gameName, chessGame);
            gameDAO.updateGame(updatedGame);
        }

        return new JoinResult();
    }

    public ListResult list(ListRequest listRequest) throws DataAccessException {
        String token = listRequest.authToken();

        AuthToken authToken = authDAO.getAuth(token);
        if (authToken == null) {
            throw new DataAccessException("Error: unauthorized");
        }

        Collection<Game> games = gameDAO.listGames();
        ListResult listResult = new ListResult(games);
        return listResult;
    }
}
