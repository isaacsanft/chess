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
        try (var conn = DatabaseManager.getConnection()) {
            var query = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(query)) {
                ps.setString(1, auth);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        AuthToken authToken = new AuthToken(rs.getString("authToken"), rs.getString("username"));
                        return authToken;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String auth) throws DataAccessException {
        var query = "DELETE FROM auth WHERE authToken=?";
        DatabaseManager.executeUpdate(query, auth);
    }

    @Override
    public void clear() throws DataAccessException {
        var query = "TRUNCATE auth";
        DatabaseManager.executeUpdate(query);
    }
}
