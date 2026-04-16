package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor teamColor = board.getPiece(position).getTeamColor();

        int x = position.getRow();
        int y = position.getColumn();
        int updown = 1;
        int startRow = 2;
        int promotionRow = 8;

        if (teamColor == ChessGame.TeamColor.BLACK) {
            updown = -1;
            startRow = 7;
            promotionRow = 1;
        }

        x += updown;
        ChessPosition target = new ChessPosition(x, y);
        ChessPiece targetPiece = board.getPiece(target);
        if (targetPiece == null) {
            if (x == promotionRow) {
                addPromotionMove(moves, position, target);
            } else {
                moves.add(new ChessMove(position, target, null));
            }
        }
        if (position.getRow() == startRow ) {
            x += updown;
            ChessPosition targetTwo = new ChessPosition(x, y);
            ChessPiece targetPiece2 = board.getPiece(targetTwo);
            if (targetPiece == null && targetPiece2 == null) {
                moves.add(new ChessMove(position, targetTwo, null));
            }
        }
        int[][] directions = {{updown, 1}, {updown, -1}};
        for (int[] direction : directions) {
            x = position.getRow() + direction[0];
            y = position.getColumn() + direction[1];
            if (y < 1 || y > 8) { continue; }
            target = new ChessPosition(x, y);
            targetPiece = board.getPiece(target);
            if (x == promotionRow && targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                addPromotionMove(moves, position, target); // ← and here
            } else if (targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                moves.add(new ChessMove(position, target, null));
            }
        }

        return moves;
    }

    private void addPromotionMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end) {
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(start, end, ChessPiece.PieceType.KNIGHT));
    }
}