package com.library.service;

import com.library.dao.UserDAO;
import com.library.model.user.User;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = UserDAO.getInstance();
    }

    public void addUser(User user) {
        userDAO.save(user);
    }

    public void updateUser(User user) {
        userDAO.update(user);
    }

    public void deleteUser(String name) {
        userDAO.delete(name);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public Optional<User> getUserByName(String name) {
        return userDAO.findByName(name);
    }
}