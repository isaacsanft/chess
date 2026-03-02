package dataaccess;
import model.User;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{

    private final HashMap<String, User> users = new HashMap<>();

    @Override
    public void createUser(User user) throws DataAccessException {
        String username = user.username();
        users.put(username, user);
    }

    @Override
    public User getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }
}
