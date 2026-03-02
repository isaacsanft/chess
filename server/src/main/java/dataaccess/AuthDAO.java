package dataaccess;

import model.AuthToken;

public interface AuthDAO {
    void createAuth(AuthToken auth) throws DataAccessException;
    AuthToken getAuth(String username) throws DataAccessException;
}
