package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        Collection<ChessMove> rawMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : rawMoves) {
            ChessPiece targetPiece = board.getPiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), piece);
            board.addPiece(move.getStartPosition(), null);
            if (!isInCheck(piece.getTeamColor())) {
                validMoves.add(move);
            }
            board.addPiece(move.getEndPosition(), targetPiece);
            board.addPiece(move.getStartPosition(), piece);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("No piece at that position.");
        }
        else if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("Not your turn.");
        }
        Collection<ChessMove> potentialMoves = validMoves(move.getStartPosition());
        if (!potentialMoves.contains(move)) {
            throw new InvalidMoveException("Not a valid move.");
        }
        if (move.getPromotionPiece() == null) {
            board.addPiece(move.getEndPosition(), piece);
        }
        else if (move.getPromotionPiece() != null) {
            ChessPiece promotionPiece = new ChessPiece(teamTurn, move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotionPiece);
        }
        board.addPiece(move.getStartPosition(), null);
        if (teamTurn == TeamColor.BLACK) {
            teamTurn = TeamColor.WHITE;
        }
        else if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public ChessPosition kingPosition(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for(int row = 1; row < 9; row++) {
            for(int col = 1; col < 9; col++) {
                ChessPosition targetPosition = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece == null) {
                    continue;
                }
                if (targetPiece.getPieceType() == ChessPiece.PieceType.KING && targetPiece.getTeamColor() == teamColor) {
                    kingPosition = new ChessPosition(row, col);
                }
            }
        }
        return kingPosition;
    }


    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = kingPosition(teamColor);
        for(int row = 1; row < 9; row++) {
            for(int col = 1; col < 9; col++) {
                ChessPosition targetPosition = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece == null) {
                    continue;
                }
                if (targetPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> enemyMoves = targetPiece.pieceMoves(board, targetPosition);
                    for (ChessMove move :  enemyMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = kingPosition(teamColor);
        if (!isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> kkingMoves = validMoves(kingPosition);
        if (!kkingMoves.isEmpty()) {
            return false;
        }
        for(int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition ttargetPosition = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(ttargetPosition);
                if (targetPiece == null) {
                    continue;
                }
                if (targetPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> allyMoves = validMoves(ttargetPosition);
                    if (!allyMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        ChessPosition kingPosition = kingPosition(teamColor);
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessMove> kingMoves = validMoves(kingPosition);
        if (!kingMoves.isEmpty()) {
            return false;
        }
        for(int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition targetPosition = new ChessPosition(row, col);
                ChessPiece targetPiece = board.getPiece(targetPosition);
                if (targetPiece == null) {
                    continue;
                }
                if (targetPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> allyMoves = validMoves(targetPosition);
                    if (!allyMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}