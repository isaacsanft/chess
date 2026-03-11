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

        if (teamColor == ChessGame.TeamColor.BLACK) {
            x -= 1;
            ChessPosition target = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(target);
            if (targetPiece == null) {
                if (x == 1) {
                    moves.add(new ChessMove(position, target, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(position, target, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, target, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(position, target, ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(position, target, null));
                }
            }
            if (position.getRow() == 7 ) {
                x -= 1;
                target = new ChessPosition(x, y);
                targetPiece = board.getPiece(target);
                ChessPosition targetTwo = new ChessPosition(x + 1, y);
                ChessPiece targetPiece2 = board.getPiece(targetTwo);
                if (targetPiece == null && targetPiece2 == null) {
                    moves.add(new ChessMove(position, target, null));
                }
            }
            int[][] directions = {{-1, 1}, {-1, -1}};
            for (int[] direction : directions) {
                x = position.getRow() + direction[0];
                y = position.getColumn() + direction[1];
                if (y < 1 || y > 8) {
                    continue;
                }
                target = new ChessPosition(x, y);
                targetPiece = board.getPiece(target);
                if (x > 1) {
                    if (targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                        moves.add(new ChessMove(position, target, null));
                    }
                }
                else if (x == 1) {
                    if (targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                        moves.add(new ChessMove(position, target, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, target, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, target, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(position, target, ChessPiece.PieceType.KNIGHT));
                    }
                }
            }
        }
        else if (teamColor == ChessGame.TeamColor.WHITE) {
            x += 1;
            ChessPosition target = new ChessPosition(x, y);
            ChessPiece targetPiece = board.getPiece(target);
            if (targetPiece == null) {
                if (x == 8) {
                    moves.add(new ChessMove(position, target, ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(position, target, ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(position, target, ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(position, target, ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(position, target, null));
                }
            }
            if (position.getRow() == 2 ) {
                x += 1;
                target = new ChessPosition(x, y);
                targetPiece = board.getPiece(target);
                ChessPosition targetTwo = new ChessPosition(x + 1, y);
                ChessPiece targetPiece2 = board.getPiece(targetTwo);
                if (targetPiece == null && targetPiece2 == null) {
                    moves.add(new ChessMove(position, target, null));
                }
            }
            int[][] directions = {{1, 1}, {1, -1}};
            for (int[] direction : directions) {
                x = position.getRow() + direction[0];
                y = position.getColumn() + direction[1];
                if (y < 1 || y > 8) {
                    continue;
                }
                target = new ChessPosition(x, y);
                targetPiece = board.getPiece(target);
                if (x < 8) {
                    if (targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                        moves.add(new ChessMove(position, target, null));
                    }
                } else if (x == 8) {
                    if (targetPiece != null && targetPiece.getTeamColor() != teamColor) {
                        moves.add(new ChessMove(position, target, ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(position, target, ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(position, target, ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(position, target, ChessPiece.PieceType.KNIGHT));
                    }
                }
            }
        }
        return moves;
    }
}