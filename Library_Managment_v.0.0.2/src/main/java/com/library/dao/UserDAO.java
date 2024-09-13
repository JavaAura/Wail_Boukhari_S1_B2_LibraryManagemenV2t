package com.library.dao;

import com.library.model.user.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDAO {
    void addUser(User user);
    void updateUser(User user);
    void deleteUser(UUID userId);
    Optional<User> getUserById(UUID userId);
    Optional<User> getUserByName(String name);
    List<User> getAllUsers();
}
