package com.library.service;

import com.library.dao.UserDAO;
import com.library.dao.UserDAOImpl;
import com.library.model.user.User;
import com.library.model.user.Student;
import com.library.model.user.Professor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = UserDAOImpl.getInstance();
    }

    public void addUser(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }
        userDAO.addUser(user);
    }

    public void updateUser(User user) {
        userDAO.updateUser(user);
    }

    public void deleteUser(String name) {
        Optional<User> user = getUserByName(name);
        if (user.isPresent()) {
            userDAO.deleteUser(user.get().getId());
        } else {
            throw new IllegalArgumentException("User not found: " + name);
        }
    }

    public Optional<User> getUserById(UUID userId) {
        return userDAO.getUserById(userId);
    }

    public Optional<User> getUserByName(String name) {
        return userDAO.getUserByName(name);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public User createStudent(String name, String email, String phoneNumber, String studentId, String department) {
        return new Student(null, name, email, phoneNumber, studentId, department);
    }

    public User createProfessor(String name, String email, String phoneNumber, String department) {
        if (name == null || email == null || phoneNumber == null || department == null) {
            throw new IllegalArgumentException("All fields must be non-null for creating a professor");
        }
        return new Professor(null, name, email, phoneNumber, department);
    }
}
