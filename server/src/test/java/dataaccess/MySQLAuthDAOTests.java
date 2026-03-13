package dataaccess;

import model.AuthToken;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLAuthDAOTests {

    private static AuthDAO authDAO;

    @BeforeAll
    public static void init() throws DataAccessException {
        DatabaseManager.configureDatabase();
        authDAO = new MySQLAuthDAO();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO.clear();
    }

    @Test
    public void createAuthPositive() throws DataAccessException {
        AuthToken token = new AuthToken("test token", "test name");
        authDAO.createAuth(token);
        AuthToken getToken = authDAO.getAuth("test token");
        assertEquals("test name", getToken.username());
    }

    @Test
    public void createAuthNegative() throws DataAccessException {
        AuthToken fakeToken = new AuthToken(null, "test name");
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(fakeToken));
    }

    @Test
    public void getAuthPositive() throws DataAccessException {
        AuthToken token = new AuthToken("test token", "test name");
        authDAO.createAuth(token);
        AuthToken getToken = authDAO.getAuth("test token");
        assertNotNull(getToken);
    }

    @Test
    public void getAuthNegative() throws DataAccessException {
        AuthToken getToken = authDAO.getAuth("fake token");
        assertNull(getToken);
    }

    @Test
    public void deleteAuthPositive() throws DataAccessException {
        AuthToken token = new AuthToken("test token", "test name");
        authDAO.createAuth(token);
        authDAO.deleteAuth("test token");
        AuthToken getToken = authDAO.getAuth("test token");
        assertNull(getToken);
    }

    @Test
    public void deleteAuthNegative() throws DataAccessException {
        assertDoesNotThrow(() -> authDAO.deleteAuth("fake token"));
    }
}
