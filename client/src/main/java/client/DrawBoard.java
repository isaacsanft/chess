package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class DrawBoard {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters.
    private static final String EMPTY = " \u2003 ";
    private static Random rand = new Random();

    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        drawBoard(out, board, ChessGame.TeamColor.WHITE);
        out.print("Now Black:");
        out.println();
        drawBoard(out, board, ChessGame.TeamColor.BLACK);
    }

    public static void drawBoard(PrintStream out, ChessBoard board, ChessGame.TeamColor color) {
        out.print(ERASE_SCREEN);
        drawHeaders(out, color);
        for (int i = 0; i < 8; i++) {
            drawRow(out, i, board, color);
        }
        drawHeaders(out, color);
        out.print(RESET_BG_COLOR);
    }

    private static void drawHeaders(PrintStream out, ChessGame.TeamColor color) {
        setBlack(out);
        out.print(SET_TEXT_COLOR_WHITE);
        String[] headers;
        if (color == ChessGame.TeamColor.BLACK) {
            headers = new String[]{"h", "g", "f", "e", "d", "c", "b", "a"};
        }
        else {
            headers = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        }
        out.print(EMPTY);
        for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
            out.print(" " + headers[boardCol] + " ");
        }
        out.print(EMPTY);
        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void drawRow(PrintStream out, int row, ChessBoard board, ChessGame.TeamColor color) {
        int diplayRow = getDisplayRow(row, color);

        printBorderSquare(out, diplayRow);
        for (int i = 0; i < 8 ; i++) {
            int displayColumn = getDisplayColumn(i, color);
            String bgColor = null;
            if ((row + i) % 2 == 0) {
                bgColor = SET_BG_COLOR_WHITE;
            }
            else {
                bgColor = SET_BG_COLOR_LIGHT_GREY;
            }
            ChessPosition position = new ChessPosition(diplayRow, displayColumn);
            ChessPiece piece = board.getPiece(position);
            printSquare(out, piece, bgColor);
        }
        printBorderSquare(out, diplayRow);
        out.print(RESET_BG_COLOR);
        out.print(RESET_TEXT_COLOR);
        out.println();
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void printSquare(PrintStream out, ChessPiece piece, String color) {
        out.print(color);
        if (piece == null) {
            out.print(EMPTY);
        }
        else {
            ChessGame.TeamColor teamColor = piece.getTeamColor();
            String icon = getIcon(piece).trim();

            if (teamColor == ChessGame.TeamColor.BLACK) {
                out.print(SET_TEXT_COLOR_BLACK);
                out.print(" " + icon + " ");
            }
            else {
                out.print(SET_TEXT_COLOR_BLUE);
                out.print(" " + icon + " ");
            }
        }
        out.print(RESET_TEXT_COLOR);
    }

    private static void printBorderSquare(PrintStream out, int row) {
        setBlack(out);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(" " + row + " ");
    }

    private static int getDisplayRow(int row, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return 8 - row;
        }
        else {
            return row + 1;
        }
    }

    private static int getDisplayColumn(int col, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return col + 1;
        }
        else {
            return 8 - col;
        }
    }

    private static String getIcon(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        ChessGame.TeamColor teamColor = piece.getTeamColor();
        ChessPiece.PieceType pieceType = piece.getPieceType();
        if (teamColor == ChessGame.TeamColor.WHITE) {
            if (pieceType == ChessPiece.PieceType.PAWN) {
                return WHITE_PAWN;
            } else if (pieceType == ChessPiece.PieceType.BISHOP) {
                return WHITE_BISHOP;
            } else if (pieceType == ChessPiece.PieceType.KNIGHT) {
                return WHITE_KNIGHT;
            } else if (pieceType == ChessPiece.PieceType.QUEEN) {
                return WHITE_QUEEN;
            } else if (pieceType == ChessPiece.PieceType.KING) {
                return WHITE_KING;
            } else if (pieceType == ChessPiece.PieceType.ROOK) {
                return WHITE_ROOK;
            }
        }
        if (teamColor == ChessGame.TeamColor.BLACK) {
            if (pieceType == ChessPiece.PieceType.PAWN) {
                return BLACK_PAWN;
            } else if (pieceType == ChessPiece.PieceType.BISHOP) {
                return BLACK_BISHOP;
            } else if (pieceType == ChessPiece.PieceType.KNIGHT) {
                return BLACK_KNIGHT;
            } else if (pieceType == ChessPiece.PieceType.QUEEN) {
                return BLACK_QUEEN;
            } else if (pieceType == ChessPiece.PieceType.KING) {
                return BLACK_KING;
            } else if (pieceType == ChessPiece.PieceType.ROOK) {
                return BLACK_ROOK;
            }
        }
        return EMPTY;
    }
}
