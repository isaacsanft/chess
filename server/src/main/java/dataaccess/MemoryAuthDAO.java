package dataaccess;

import model.AuthToken;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{

    private final HashMap<String, AuthToken> tokens = new HashMap<>();

    @Override
    public void createAuth(AuthToken auth) throws DataAccessException {
        String token = auth.authToken();
        tokens.put(token, auth);
    }

    @Override
    public AuthToken getAuth(String token) throws DataAccessException {
        return tokens.get(token);
    }

    @Override
    public void deleteAuth(String auth) throws DataAccessException {
        tokens.remove(auth);
    }

    @Override
    public void clear() throws DataAccessException {
        tokens.clear();
    }
}
