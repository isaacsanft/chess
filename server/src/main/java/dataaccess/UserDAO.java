package dataaccess;

import model.User;

public interface UserDAO {
    void createUser(User user);
    User getUser(String username);
    void updateUser(User user);
    void clear();
}
