package client;

import chess.*;

public class ClientMain {
    public static void main(String[] args) throws ResponseException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        ServerFacade server = new ServerFacade(serverUrl);
        Repl repl = new Repl(server, serverUrl);
        repl.run();
    }
}
