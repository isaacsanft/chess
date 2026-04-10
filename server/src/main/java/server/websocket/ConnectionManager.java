package server.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ArrayList<Connection>> games = new ConcurrentHashMap<>();

    public void add(Session session, Integer gameID, String username) {
        ArrayList<Connection> connections = games.get(gameID);
        if (connections == null) {
            games.put(gameID, new ArrayList<>());
            connections = games.get(gameID);
        }
        Connection connection = new Connection(username, session);
        connections.add(connection);

    }

    public void remove(Session session, Integer gameID) {
        ArrayList<Connection> connections = games.get(gameID);
        if (connections != null) {
            var iterator = connections.iterator();
            while (iterator.hasNext()) {
                Connection connection = iterator.next();
                if (connection.session.equals(session)) {
                    iterator.remove();
                }
            }
        }
    }

    public void broadcast(Integer gameID, String excludeUserName, ServerMessage message) throws IOException {
        ArrayList<Connection> connections = games.get(gameID);
        for (Connection connection : connections) {
            if (connection.session.isOpen() &&
                    (excludeUserName == null || !connection.username.equals(excludeUserName))) {
                String jsonMessage = new Gson().toJson(message);
                connection.send(jsonMessage);
            }
        }
    }
}