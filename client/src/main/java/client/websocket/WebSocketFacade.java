package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import client.ResponseException;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {

    Session session;
    private ServerMessageObserver observer;

    public WebSocketFacade(String url, ServerMessageObserver observer) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.observer = observer;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                    observer.notify(serverMessage);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws ResponseException {
        try {
            MakeMoveCommand makeMoveCommand = new MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void leave(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    public void resign(String authToken, Integer gameID) throws ResponseException {
        try {
            UserGameCommand userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }
    }
}
