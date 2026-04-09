package server;

import dataaccess.*;
import handlers.*;
import io.javalin.*;
import result.ListResult;
import server.websocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;

import javax.xml.crypto.Data;

public class Server {

    private final Javalin javalin;

    public Server() {

        try {
            DatabaseManager.configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Unable to create database.");
        }

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        UserDAO userDAO = new MySQLUserDAO();
        AuthDAO authDAO = new MySQLAuthDAO();
        GameDAO gameDAO = new MySQLGameDAO();

        UserService user = new UserService(userDAO, authDAO);
        GameService game = new GameService(userDAO, authDAO, gameDAO);
        ClearService clear = new ClearService(userDAO, authDAO, gameDAO);

        RegisterHandler registerHandler = new RegisterHandler(user);
        ClearHandler clearHandler = new ClearHandler(clear);
        LoginHandler loginHandler = new LoginHandler(user);
        LogoutHandler logoutHandler = new LogoutHandler(user);
        CreateHandler createHandler = new CreateHandler(game);
        JoinHandler joinHandler = new JoinHandler(game);
        ListHandler listHandler = new ListHandler(game);

        javalin.delete("/db", clearHandler::clear);
        javalin.post("/user", registerHandler::register);
        javalin.post("/session", loginHandler::login);
        javalin.delete("/session", logoutHandler::logout);
        javalin.post("/game", createHandler::create);
        javalin.put("/game", joinHandler::join);
        javalin.get("/game", listHandler::list);
    }

    public int run(int desiredPort) {
        WebSocketHandler webSocketHandler = new WebSocketHandler();
        javalin.ws("/ws", ws -> {ws.onConnect(webSocketHandler);
        ws.onMessage(webSocketHandler);
        ws.onClose(webSocketHandler);});
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
