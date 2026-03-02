package dataaccess;

import model.AuthToken;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{

    private final HashMap<String, AuthToken> tokens = new HashMap<>();

    @Override
    public void createAuth(AuthToken auth) throws DataAccessException {
        String username = auth.username();
        tokens.put(username, auth);
    }

    @Override
    public AuthToken getAuth(String username) throws DataAccessException {
        return tokens.get(username);
    }
}
