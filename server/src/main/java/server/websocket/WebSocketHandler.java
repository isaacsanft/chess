package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.AuthToken;
import model.Game;
import model.User;
import org.eclipse.jetty.websocket.api.Session;
import server.websocket.messages.ErrorMessage;
import server.websocket.messages.LoadGameMessage;
import server.websocket.messages.NotificationMessage;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;

import java.io.IOException;
import java.util.Collection;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();
    private MySQLAuthDAO authDAO = new MySQLAuthDAO();
    private MySQLGameDAO gameDAO = new MySQLGameDAO();
    private MySQLUserDAO userDAO = new MySQLUserDAO();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws IOException {
        String message = ctx.message();
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT:
                String token = command.getAuthToken();
                Integer gameID = command.getGameID();
                Session session = ctx.session;

                connect(token, gameID, session);
                break;

            case MAKE_MOVE:
                MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                ChessMove move = makeMoveCommand.move;
                makeMove(makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID(), move, ctx.session);
                break;

            case LEAVE:
                leave(command.getAuthToken(), command.getGameID(), ctx.session);
                break;

            case RESIGN:
                resign(command.getAuthToken(), command.getGameID(), ctx.session);
                break;
        }
    }

    private void connect(String token, Integer gameID, Session session) {
        try {
            AuthToken authToken = authDAO.getAuth(token);
            if (authToken == null) {
                throw new DataAccessException("Unauthorized");
            }
            String username = authToken.username();

            Game game = gameDAO.getGame(gameID);
            if (game == null) {
                throw new DataAccessException("Game does not exist.");
            }

            connectionManager.add(session, gameID, username);

            LoadGameMessage loadGameMessage = new LoadGameMessage(game.game());
            String jsonMessage = new Gson().toJson(loadGameMessage);
            session.getRemote().sendString(jsonMessage);

            String message = username + "  joined the game";
            NotificationMessage notificationMessage = new NotificationMessage(message);
            connectionManager.broadcast(gameID, username, notificationMessage);


        } catch (DataAccessException e) {
            try {
                ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
                String jsonErrorMessage = new Gson().toJson(errorMessage);
                session.getRemote().sendString(jsonErrorMessage);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void makeMove(String token, Integer gameID, ChessMove move, Session session) throws IOException {
        try {
            AuthToken authToken = authDAO.getAuth(token);
            if (authToken == null) {
                throw new DataAccessException("Unauthorized");
            }
            String username = authToken.username();

            Game game = gameDAO.getGame(gameID);
            if (game == null) {
                throw new DataAccessException("Game does not exist.");
            }
            ChessGame chessGame = game.game();
            ChessGame.TeamColor turnColor = chessGame.getTeamTurn();
            ChessGame.TeamColor userColor = null;
            if (username.equals(game.blackUsername())) {
                userColor = ChessGame.TeamColor.BLACK;
            } else if (username.equals(game.whiteUsername())) {
                userColor = ChessGame.TeamColor.WHITE;
            }
            if (userColor == null) {
                throw new DataAccessException("Error: You are an observer.");
            } else if (userColor != turnColor) {
                throw new DataAccessException("Error: It's not your turn.");
            }
            if (chessGame.isInCheckmate(turnColor) || chessGame.isInStalemate(turnColor)) {
                throw new DataAccessException("Error: Game over.");
            }
            if (chessGame.isGameOver()) {
                throw new DataAccessException("Error: Game over.");
            }
            ChessPosition startPosition = move.getStartPosition();
            Collection<ChessMove> validMoves = chessGame.validMoves(startPosition);
            if (!validMoves.contains(move)) {
                throw new DataAccessException("Error: That's not a valid move.");
            }
            chessGame.makeMove(move);
            boolean over = false;
            if (chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                    chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                over = true;
                connectionManager.broadcast(gameID, null, new NotificationMessage("Checkmate. Game over."));
            } else if (chessGame.isInStalemate(ChessGame.TeamColor.WHITE) ||
                    chessGame.isInStalemate(ChessGame.TeamColor.BLACK)) {
                over = true;
                connectionManager.broadcast(gameID, null, new NotificationMessage("Stalemate. Game over."));
            }
            if (over) {
                chessGame.setGameOver(true);
                gameDAO.updateGame(game);
            }

            gameDAO.updateGame(game);

            LoadGameMessage loadGameMessage = new LoadGameMessage(game.game());
            String jsonMessage = new Gson().toJson(loadGameMessage);
            connectionManager.broadcast(gameID, null, loadGameMessage);

            String message = positionString(move, username);
            NotificationMessage notificationMessage = new NotificationMessage(message);
            connectionManager.broadcast(gameID, username, notificationMessage);

        } catch (DataAccessException | InvalidMoveException e) {
            try {
                ErrorMessage errorMessage = new ErrorMessage("Error: " + e.getMessage());
                session.getRemote().sendString(new Gson().toJson(errorMessage));
            } catch (IOException i) {
                throw new RuntimeException(i);
            }
        }
    }

    public void leave(String token, Integer gameID, Session session) {
        try {
            AuthToken authToken = authDAO.getAuth(token);
            Game game = gameDAO.getGame(gameID);
            String username = authToken.username();
            connectionManager.remove(session, gameID);

            if (username.equals(game.whiteUsername())) {
                Game updatedGame = new Game (gameID, null, game.blackUsername(), game.gameName(), game.game());
                gameDAO.updateGame(updatedGame);
            }
            else if (username.equals(game.blackUsername())) {
                Game updatedGame = new Game (gameID, game.whiteUsername(), null, game.gameName(), game.game());
                gameDAO.updateGame(updatedGame);
            }

            String message = username + " left the game.";
            NotificationMessage notificationMessage = new NotificationMessage(message);
            connectionManager.broadcast(gameID, username, notificationMessage);

        } catch (DataAccessException | IOException e) {
            sendErrorMessage(session, "Error: " + e.getMessage());
        }
    }

    public void resign(String token, Integer gameID, Session session) {
        try {
            AuthToken authToken = authDAO.getAuth(token);
            Game game = gameDAO.getGame(gameID);
            String username = authToken.username();
            ChessGame chessGame = game.game();
            ChessGame.TeamColor turnColor = chessGame.getTeamTurn();
            if (!(username.equals(game.whiteUsername()) || username.equals(game.blackUsername()))) {
                throw new DataAccessException("Error: Observing.");
            }
            if (chessGame.isGameOver()) {
                throw new DataAccessException("Error: Game over.");
            }
            chessGame.setGameOver(true);
            gameDAO.updateGame(game);
            String message = username + "resigned. Game over.";
            NotificationMessage notificationMessage = new NotificationMessage(message);
            connectionManager.broadcast(gameID, null, notificationMessage);
        } catch (DataAccessException | IOException e) {
            sendErrorMessage(session, "Error: " + e.getMessage());
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private String positionString(ChessMove move, String username) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        int r1 = start.getRow();
        int c1 = start.getColumn();
        int r2 = end.getRow();
        int c2 = end.getColumn();
        return username + " moved from [" + r1 + ", " + c1 +"] to [" + r2 + ", " + c2 + "]";
    }

    private void sendErrorMessage(Session session, String message) {
        try {
            ErrorMessage errorMessage = new ErrorMessage(message);
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        } catch (IOException e) {
            System.out.println("Unable to create error message.");
        }
    }

}