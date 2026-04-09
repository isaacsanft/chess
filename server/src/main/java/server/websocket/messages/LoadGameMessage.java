package server.websocket.messages;

import chess.ChessGame;
import websocket.messages.ServerMessage;

public class LoadGameMessage extends ServerMessage {
    public final ChessGame game;
    public LoadGameMessage(ChessGame game) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
    }
    public ChessGame getGame() {
        return game;
    }
}
