package model;

public record User(String username, String password, String email) {
    User update(String newUsername, String newPassword, String newEmail) {
        return new User(newUsername, newPassword, newEmail);
    }
}
