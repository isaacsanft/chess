package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;
import chess.ChessGame;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> calculate_moves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team_color = board.getPiece(position).getTeamColor();

        int[][] directions = {{1, 2}, {1, -2}, {-1, 2}, {-1, -2}, {2, 1}, {2, -1}, {-2, 1}, {-2, -1}};

        for (int[] direction : directions) {
            int x = position.getRow();
            int y = position.getColumn();

            x += direction[0];
            y += direction[1];

            if (x > 8 || x < 1 || y > 8 || y < 1) {
                continue;
            }
            ChessPosition target = new ChessPosition(x, y);
            ChessPiece target_piece = board.getPiece(target);
            if (target_piece == null) {
                moves.add(new ChessMove(position, target, null));
            }
            else if (target_piece.getTeamColor() == team_color) {
                continue;
            }
            else {
                moves.add(new ChessMove(position, target, null));
            }
        }
        return moves;
    }
}
