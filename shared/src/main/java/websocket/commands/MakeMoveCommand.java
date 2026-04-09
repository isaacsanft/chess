package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private static CommandType commandType;
    public ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(commandType = CommandType.MAKE_MOVE, authToken, gameID);
        this.move = move;
    }

}
