package dataaccess;

import model.AuthToken;

public interface AuthDAO {
    void createAuth(AuthToken auth);
    AuthToken getAuth(String username);
}
