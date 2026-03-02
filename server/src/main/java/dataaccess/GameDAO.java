package dataaccess;

import model.Game;

public interface GameDAO {
    void createGame(Game game);
    Game getGame(int gameID);
    void updateGame(Game game);
    void clear();
}
