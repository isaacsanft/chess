package server.websocket;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.*;
import model.AuthToken;
import model.Game;
import org.eclipse.jetty.websocket.api.Session;
import server.websocket.messages.ErrorMessage;
import server.websocket.messages.LoadGameMessage;
import server.websocket.messages.NotificationMessage;
import websocket.commands.UserGameCommand;

import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final ConnectionManager connectionManager = new ConnectionManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        String message = ctx.message();
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);

        switch (command.getCommandType()) {
            case CONNECT:
                String token = command.getAuthToken();
                Integer gameID = command.getGameID();
                Session session = ctx.session;

                connect(token, gameID, session);
                break;
        }
    }

    private void connect(String token, Integer gameID, Session session) {
        try {
            MySQLAuthDAO authDAO = new MySQLAuthDAO();
            MySQLGameDAO gameDAO = new MySQLGameDAO();

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

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

}