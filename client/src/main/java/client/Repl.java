package client;

import chess.*;
import client.websocket.ServerMessageObserver;
import client.websocket.WebSocketFacade;
import model.Game;
import request.*;
import result.*;
import server.websocket.messages.ErrorMessage;
import server.websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import server.websocket.messages.LoadGameMessage;

import java.util.*;

public class Repl implements ServerMessageObserver {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken = null;
    private List<Integer> gameIDs = new ArrayList<>();
    private WebSocketFacade webSocketFacade;
    private ChessGame.TeamColor boardColor = ChessGame.TeamColor.WHITE;
    private Integer gameID;
    private ChessGame chessGame = null;

    public Repl(ServerFacade server, String serverUrl) throws ResponseException {
        this.server = server;
        this.serverUrl = serverUrl;
        try {
            this.webSocketFacade = new WebSocketFacade(serverUrl, this);
        } catch (ResponseException e) {
            System.out.println("Error creating connection.");
        }
    }

    private void printState() {
        if (state == State.SIGNEDOUT) {
            System.out.print("\n[LOGGED_OUT] >>> ");
        }
        else {
            System.out.print("\n[LOGGED_IN] >>> ");
        }
    }

    public void run() {
        System.out.print("Welcome to CS 240 Chess. Type Help to get started.");
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equalsIgnoreCase("quit")) {
            printState();

            String line = scanner.nextLine();
            result = eval(line);
            System.out.print(result);
        }

    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "help" -> help();
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "quit" -> "quit";
                case "m", "move", "make" -> move(params);
                case "leave" -> leave();
                case "res", "resign" -> resign();
                case "r", "redraw" -> redraw();
                case "hl", "highlight" -> highlight(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return "Please try again";
        }
    }

    private String help() {
        StringBuilder stringBuilder = new StringBuilder();
        if (state == State.SIGNEDOUT) {
            stringBuilder.append("register <USERNAME> <PASSWORD> <EMAIL> - to create an account\n");
            stringBuilder.append("login <USERNAME> <PASSWORD> - to play chess\n");
            stringBuilder.append("quit - playing chess\n");
            stringBuilder.append("help - with possible commands\n");
        }
        else if (state == State.SIGNEDIN){
            stringBuilder.append("create <NAME> - a game\n");
            stringBuilder.append("list - games\n");
            stringBuilder.append("join <ID> [WHITE|BLACK|OBSERVE] - a game\n");
            stringBuilder.append("logout - when you are done\n");
            stringBuilder.append("quit - playing chess\n");
            stringBuilder.append("help - with possible commands\n");
        }
        else {
            stringBuilder.append("Options:\n");
            stringBuilder.append("Highlight legal moves: \"hl\", \"highlight\" <position> (e.g. f5)\n");
            stringBuilder.append("Make a move: \"m\", \"move\", \"make\" <source> <destination> <optional promotion> (e.g. f5 e4 q)\n");
            stringBuilder.append("Redraw Chess Board: \"r\", \"redraw\"\n");
            stringBuilder.append("Change color scheme: \"c\", \"colors\" <color number>\n");
            stringBuilder.append("Resign from game: \"res\", \"resign\"\n");
            stringBuilder.append("Leave game: \"leave\"\n");
        }
        return stringBuilder.toString();
    }

    private String register(String[] params) throws ResponseException {
        if (params.length == 3) {
            try {
                String username = params[0];
                String password = params[1];
                String email = params[2];
                RegisterRequest request = new RegisterRequest(username, password, email);
                RegisterResult result = server.register(request);
                this.authToken = result.authToken();
                this.state = State.SIGNEDIN;
                return "Registration Successful" + "\n" +"Logged in as " + username + "\n";
            } catch (Exception e) {
                return "User already exists with username.";
            }
        }
        else {
            return "Please follow format: register <USERNAME> <PASSWORD> <EMAIL>";
        }
    }

    private String login(String[] params) throws ResponseException {
        if (params.length == 2) {
            try {
                String username = params[0];
                String password = params[1];
                LoginRequest request = new LoginRequest(username, password);
                LoginResult result = server.login(request);
                this.authToken = result.authToken();
                this.state = State.SIGNEDIN;
                return "Logged in as " + username + "\n";
            } catch (Exception e) {
                return "Invalid login credentials.";
            }
        }
        else {
            return "Please follow format: login <USERNAME> <PASSWORD>";
        }
    }

    private String redraw() {
        if (chessGame != null) {
            System.out.println();
            DrawBoard.drawBoard(System.out, chessGame.getBoard(), boardColor);
            System.out.println();
            return "";
        }
        else {
            return "Error: Unable to redraw board.";
        }
    }

    private String logout() throws ResponseException {
        try {
            LogoutRequest request = new LogoutRequest(this.authToken);
            LogoutResult result = server.logout(request);
            this.state = State.SIGNEDOUT;
            return "Logout Successful" + "\n";
        } catch (Exception e) {
            return "Invalid. Please try again.";
        }
    }

    private String create(String[] params) throws ResponseException {
        if (params.length == 1) {
            try {
                String gameName = params[0];
                CreateRequest request = new CreateRequest(gameName, this.authToken);
                CreateResult result = server.create(request);
                Integer gameID = result.gameID();
                gameIDs.add(gameID);
                return "Successfully Created Game: " + gameName + "\n";
            } catch (Exception e) {
                return "Game with same name already exists.";
            }
        }
        else {
            return "Please follow format: create <NAME>";
        }
    }

    private String join(String[] params) throws ResponseException {
        if (params.length == 2 &&
                ( "WHITE".equalsIgnoreCase(params[1]) ||
                "BLACK".equalsIgnoreCase(params[1]) ||
                "OBSERVE".equalsIgnoreCase(params[1]))) {
            try {
                int index = Integer.parseInt(params[0]) - 1;
                if (index < 0 || index >= gameIDs.size()) {
                    return "Game Does Not Exist: Please run 'list' to see valid game numbers.";
                }
                Integer gameID = gameIDs.get(index);
                String perspective = params[1];
                ChessGame.TeamColor teamColor = null;
                if ("WHITE".equalsIgnoreCase(perspective)) {
                    teamColor = ChessGame.TeamColor.WHITE;
                }
                else if ("BLACK".equalsIgnoreCase(perspective)) {
                    teamColor = ChessGame.TeamColor.BLACK;
                    boardColor = ChessGame.TeamColor.BLACK;
                }
                if (teamColor != null) {
                    JoinRequest request = new JoinRequest(teamColor, gameID, this.authToken);
                    JoinResult result = server.join(request);
                }
                webSocketFacade.connect(authToken, gameID);
                this.state = State.INGAME;
                this.gameID = gameID;
                return "Successfully Joined Game" + "\n";
            } catch (Exception e) {
                return "Player slot is not open.";
            }
        }
        else {
            return "Please follow format: JOIN <GAMEID> <WHITE|BLACK|OBSERVE>";
        }
    }

    private String list() {
        try {
            ListRequest request = new ListRequest(this.authToken);
            ListResult result = server.list(request);
            gameIDs.clear();
            Collection<Game> games = result.games();
            StringBuilder stringBuilder = new StringBuilder();
            int it = 1;
            for (Game game : games) {
                gameIDs.add(game.gameID());
                String gameName = game.gameName();
                stringBuilder.append(it).append(". ").append(gameName);
                stringBuilder.append("  Players: White: ").append(game.whiteUsername()).append("  Black: ").append(game.blackUsername());
                it++;
            }
            return stringBuilder.toString();
        } catch (ResponseException e) {
            return "Invalid. Please try again.";
        }
    }

    public String move(String[] params) {
        if (params.length != 2 && params.length != 3) {
            return "Error: Please enter <start> <end> <optional promotion> (e.g. move e2 e4 r)";
        }
        try {
            String start = params[0];
            String end = params[1];
            ChessPiece.PieceType promotionType = null;
            if (params.length == 3) {
                String promotion = params[2];
                promotionType = findPromotion(promotion);
            }
            ChessPosition startPosition = findPosition(start);
            ChessPosition endPosition = findPosition(end);
            ChessMove move = new ChessMove(startPosition, endPosition, promotionType);
            webSocketFacade.makeMove(authToken, gameID, move);
            return "";
        } catch (Exception e) {
            return "Error: Please enter <start> <end> <optional promotion> (e.g. move e2 e4 r)";
        }
    }

    public String leave() throws ResponseException {
        webSocketFacade.leave(authToken, gameID);
        this.state = State.SIGNEDIN;
        this.gameID = null;
        return "Left the game.\n";
    }

    public String resign() throws ResponseException {
        webSocketFacade.resign(this.authToken, this.gameID);
        return "";
    }

    public String highlight(String[] params) {
        if (params.length != 1) {
            return "Error: Follow format <position> (e.g. hl e5)";
        }
        ChessPosition position = findPosition(params[0]);
        Collection<ChessMove> validMoves = chessGame.validMoves(position);
        DrawBoard.drawHighlightedBoard(System.out, chessGame.getBoard(), boardColor, validMoves);
        return "";
    }

    public ChessPosition findPosition(String position) {
        String letter = String.valueOf(position.charAt(0));
        int col = 0;
        int row = Character.getNumericValue(position.charAt(1));
        if (letter.equalsIgnoreCase("a")) { col = 1;}
        else if (letter.equalsIgnoreCase("b")) { col = 2;}
        else if (letter.equalsIgnoreCase("c")) { col = 3;}
        else if (letter.equalsIgnoreCase("d")) { col = 4;}
        else if (letter.equalsIgnoreCase("e")) { col = 5;}
        else if (letter.equalsIgnoreCase("f")) { col = 6;}
        else if (letter.equalsIgnoreCase("g")) { col = 7;}
        else if (letter.equalsIgnoreCase("h")) { col = 8;}
        ChessPosition chessPosition = new ChessPosition(row, col);
        return chessPosition;
    }

    public ChessPiece.PieceType findPromotion(String letter) {
        ChessPiece.PieceType pieceType = null;
        if (letter.equalsIgnoreCase("q")) { pieceType = ChessPiece.PieceType.QUEEN;}
        else if (letter.equalsIgnoreCase("b")) { pieceType = ChessPiece.PieceType.BISHOP;}
        else if (letter.equalsIgnoreCase("r")) { pieceType = ChessPiece.PieceType.ROOK;}
        else if (letter.equalsIgnoreCase("k")) { pieceType = ChessPiece.PieceType.KNIGHT;}
        return pieceType;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME:
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                ChessGame game = loadGameMessage.getGame();
                this.chessGame = game;
                ChessBoard board = game.getBoard();
                System.out.println();
                DrawBoard.drawBoard(System.out, board, boardColor);
                System.out.println();
                break;
            case NOTIFICATION:
                NotificationMessage notificationMessage = (NotificationMessage) message;
                String notification = notificationMessage.getMessage();
                System.out.println(notification);
                printState();
                break;
            case ERROR:
                ErrorMessage errorMessage = (ErrorMessage) message;
                String error = errorMessage.getErrorMessage();
                System.out.println("Error: " + error);
                printState();
                break;
        }
    }
}
