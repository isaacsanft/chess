package dataaccess;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

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

}
