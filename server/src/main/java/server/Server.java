package server;

import dataaccess.*;
import handlers.*;
import io.javalin.*;
import result.ListResult;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

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
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
