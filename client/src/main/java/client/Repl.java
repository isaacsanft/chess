package client;

import chess.ChessBoard;
import chess.ChessGame;
import model.Game;
import request.*;
import result.*;

import java.util.*;

public class Repl {
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;
    private String authToken = null;
    private List<Integer> gameIDs = new ArrayList<>();

    public Repl(ServerFacade server, String serverUrl) {
        this.server = server;
        this.serverUrl = serverUrl;
    }


    public void run() {
        System.out.print("Welcome to CS 240 Chess. Type Help to get started.");
        Scanner scanner = new Scanner(System.in);
        String result = "";

        while (!result.equalsIgnoreCase("quit")) {
            if (state == State.SIGNEDOUT) {
                System.out.print("\n[LOGGED_OUT] >>> ");
            }
            else {
                System.out.print("\n[LOGGED_IN] >>> ");
            }

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
        else {
            stringBuilder.append("create <NAME> - a game\n");
            stringBuilder.append("list - games\n");
            stringBuilder.append("join <ID> [WHITE|BLACK|OBSERVE] - a game\n");
            stringBuilder.append("logout - when you are done\n");
            stringBuilder.append("quit - playing chess\n");
            stringBuilder.append("help - with possible commands\n");
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
                return "Registration Successful" + "\n" +"Logged in as" + username + "\n";
            } catch (Exception e) {
                return "Invalid. Please try again.";
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
                return "Logged in as" + username + "\n";
            } catch (Exception e) {
                return "Invalid. Please try again.";
            }
        }
        else {
            return "Please follow format: login <USERNAME> <PASSWORD>";
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
                return "Invalid. Please try again.";
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
                ChessGame.TeamColor boardColor = ChessGame.TeamColor.WHITE;
                if ("WHITE".equalsIgnoreCase(perspective)) {
                    teamColor = ChessGame.TeamColor.WHITE;
                }
                else if ("BLACK".equalsIgnoreCase(perspective)) {
                    teamColor = ChessGame.TeamColor.BLACK;
                    boardColor = ChessGame.TeamColor.BLACK;
                }
                JoinRequest request = new JoinRequest(teamColor, gameID, this.authToken);
                JoinResult result = server.join(request);
                ChessBoard board = new ChessBoard();
                board.resetBoard();
                System.out.println(); // Add some breathing room
                DrawBoard.drawBoard(System.out, board, boardColor);
                System.out.println();
                return "Successfully Joined Game" + "\n";
            } catch (Exception e) {
                return "Invalid. Please try again.";
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
                stringBuilder.append(it).append(". ").append(gameName).append("\n");
                it++;
            }
            return stringBuilder.toString();
        } catch (ResponseException e) {
            return "Invalid. Please try again.";
        }
    }
}
