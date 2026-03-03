package server;

import dataaccess.*;
import handlers.ClearHandler;
import handlers.RegisterHandler;
import io.javalin.*;
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

        javalin.delete("/db", clearHandler::clear);
        javalin.post("/user", registerHandler::register);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
