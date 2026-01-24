package chess.piececalculators;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class BishopMovesCalculator implements PieceMovesCalculator {

    @Override
    public Collection<ChessMove> calculate_moves(ChessBoard board, ChessPosition position) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor team_color = board.getPiece(position).getTeamColor();

        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

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
                ChessPiece target_piece = board.getPiece(target);
                if (target_piece == null) {
                    moves.add(new ChessMove(position, target, null));
                }
                else if (target_piece.getTeamColor() == team_color) {
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
