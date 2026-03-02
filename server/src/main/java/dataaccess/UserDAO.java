package dataaccess;

import model.User;

public interface UserDAO {
    void createUser(User user) throws DataAccessException;
    User getUser(String username) throws DataAccessException;
    void clear() throws DataAccessException;
}
