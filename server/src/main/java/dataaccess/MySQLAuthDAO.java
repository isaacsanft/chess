package dataaccess;

import model.AuthToken;

public class MySQLAuthDAO implements AuthDAO {
    @Override
    public void createAuth(AuthToken auth) throws DataAccessException {
        var query = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        DatabaseManager.executeUpdate(query, auth.authToken(), auth.username());
    }

    @Override
    public AuthToken getAuth(String auth) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String auth) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
