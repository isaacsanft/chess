package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor teamColor = board.getPiece(position).getTeamColor();

        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}};

        for (int[] direction : directions) {
            int x = position.getRow();
            int y = position.getColumn();
            while (true) {
                x += direction[0];
                y += direction[1];
                if (x > 8 || x < 1 || y > 8 || y < 1) {
                    break;
                }
                ChessPosition target = new ChessPosition(x, y);
                ChessPiece targetPiece = board.getPiece(target);
                if (targetPiece == null) {
                    moves.add(new ChessMove(position, target, null));
                }
                else if (targetPiece.getTeamColor() == teamColor) {
                    break;
                }
                else {
                    moves.add(new ChessMove(position, target, null));
                    break;
                }
            }
        }
        return moves;
    }
}