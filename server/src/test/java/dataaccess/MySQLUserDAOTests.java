package dataaccess;

import model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLUserDAOTests {

    private static UserDAO userDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        DatabaseManager.configureDatabase();
        userDAO = new MySQLUserDAO();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO.clear();
    }

    @Test
    public void createUserPositive() throws DataAccessException {
        User user = new User("test name", "test password", "test@email.com");
        userDAO.createUser(user);
        User getUser = userDAO.getUser("test name");
        assertNotEquals("test password", getUser.password());
        assertTrue(BCrypt.checkpw("test password", getUser.password()));
    }

    @Test
    public void createUserNegative() throws DataAccessException {
        User user = new User("test name", "test password", "test@email.com");
        userDAO.createUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.createUser(user));
    }

    @Test
    public void getUserPositive() throws DataAccessException {
        User user = new User("test name", "test password", "test@email.com");
        userDAO.createUser(user);
        User getUser = userDAO.getUser("test name");
        assertNotNull(getUser);
    }

    @Test
    public void getUserNegative() throws DataAccessException {
        User getUser = userDAO.getUser("fake user");
        assertNull(getUser);
    }

    @Test
    public void clearPositive() throws DataAccessException {
        userDAO.createUser(new User("test name 1", "pass 1", "test1@email.com"));
        userDAO.createUser(new User("test name 2", "pass 2", "test2@email.com"));
        userDAO.clear();
        assertNull(userDAO.getUser("test name 1"));
        assertNull(userDAO.getUser("test name 2"));
    }
}
