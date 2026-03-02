package dataaccess;

import model.AuthToken;

public interface AuthDAO {
    void createAuth(AuthToken auth) throws DataAccessException;
    AuthToken getAuth(String auth) throws DataAccessException;
    void deleteAuth(String auth) throws DataAccessException;
    void clear() throws DataAccessException;
}
