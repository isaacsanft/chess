package chess.piececalculators;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> calculate_moves(ChessBoard board, ChessPosition position);
}
