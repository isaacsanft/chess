package dataaccess;

import model.User;
import org.mindrot.jbcrypt.BCrypt;

public class MySQLUserDAO implements UserDAO {
    @Override
    public void createUser(User user) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var query = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        DatabaseManager.executeUpdate(query, user.username(), hashedPassword, user.email());
    }

    @Override
    public User getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var query = "SELECT username, password, email FROM user WHERE username=?";
            try (var ps = conn.prepareStatement(query)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User user = new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                        return user;
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        var query = "TRUNCATE user";
        DatabaseManager.executeUpdate(query);
    }
}
