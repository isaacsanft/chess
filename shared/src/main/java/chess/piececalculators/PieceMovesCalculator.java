package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;
import chess.ChessGame;

import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position);

    default void addSlidingMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition position, int[][] directions) {
        ChessGame.TeamColor teamColor = board.getPiece(position).getTeamColor();
        for (int[] direction : directions) {
            int x = position.getRow();
            int y = position.getColumn();
            while (true) {
                x += direction[0];
                y += direction[1];
                if (x > 8 || x < 1 || y > 8 || y < 1) { break; }
                ChessPosition target = new ChessPosition(x, y);
                ChessPiece targetPiece = board.getPiece(target);
                if (targetPiece == null) {
                    moves.add(new ChessMove(position, target, null));
                }
                else if (targetPiece.getTeamColor() != teamColor) {
                    moves.add(new ChessMove(position, target, null));
                    break;
                }
                else {
                    break;
                }
            }
        }
    }

    default void addStepMoves(Collection<ChessMove> moves, ChessBoard board, ChessPosition position, int[][] directions) {
        ChessGame.TeamColor teamColor = board.getPiece(position).getTeamColor();
        for (int[] direction : directions) {
            int x = position.getRow() + direction[0];
            int y = position.getColumn() + direction[1];
            if (x > 8 || x < 1 || y > 8 || y < 1) { continue; }
            ChessPosition target = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(target);
            if (targetPiece == null || targetPiece.getTeamColor() != teamColor) {
                moves.add(new ChessMove(position, target, null));
            }
        }
    }
}