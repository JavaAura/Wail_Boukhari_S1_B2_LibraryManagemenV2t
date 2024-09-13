package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.library.model.user.Professor;
import com.library.model.user.Student;
import com.library.model.user.User;

public class UserDAO {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private static volatile UserDAO instance;

    private UserDAO() {}

    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) {
                    instance = new UserDAO();
                }
            }
        }
        return instance;
    }

    public void save(User user) {
        LOGGER.info("Attempting to save user: " + user.getName());
        try (Connection conn = PostgresConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                saveUserToLibraryUsers(conn, user);
                saveUserToSpecificTable(conn, user);
                conn.commit();
                LOGGER.info("User saved successfully: " + user.getName());
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            handleException("Error saving user", e);
        }
    }

    public void update(User user) {
        try (Connection conn = PostgresConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                updateLibraryUsers(conn, user);
                updateSpecificUserTable(conn, user);
                conn.commit();
                LOGGER.info("User updated successfully: " + user.getName());
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            handleException("Error updating user", e);
        }
    }

    public void delete(String name) {
        try (Connection conn = PostgresConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                deleteUserFromTables(conn, name);
                conn.commit();
                LOGGER.info("User deleted successfully: " + name);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            handleException("Error deleting user", e);
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String[] tables = {"student_users", "professor_users"};
        for (String table : tables) {
            String sql = "SELECT * FROM " + table;
            try (Connection conn = PostgresConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(createUserFromResultSet(rs, table));
                }
            } catch (SQLException e) {
                handleException("Error finding all users", e);
            }
        }
        return users;
    }

    public Optional<User> findByName(String name) {
        String[] tables = {"student_users", "professor_users"};
        for (String table : tables) {
            String sql = "SELECT * FROM " + table + " WHERE name = ?";
            try (Connection conn = PostgresConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(createUserFromResultSet(rs, table));
                    }
                }
            } catch (SQLException e) {
                handleException("Error finding user by name", e);
            }
        }
        return Optional.empty();
    }

    public boolean userExists(String name) {
        String sql = "SELECT COUNT(*) FROM library_users WHERE name = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            handleException("Error checking user existence", e);
            return false;
        }
    }

    private void saveUserToLibraryUsers(Connection conn, User user) throws SQLException {
        String sql = "INSERT INTO library_users (id, name, email, phone_number, borrowing_limit) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            UUID userId = user.getId() != null ? user.getId() : UUID.randomUUID();
            stmt.setObject(1, userId);
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setInt(5, user.getBorrowingLimit());
            stmt.executeUpdate();
        }
    }
    private void saveUserToSpecificTable(Connection conn, User user) throws SQLException {
        String sql;
        if (user instanceof Student) {
            sql = "INSERT INTO student_users (id, name, email, phone_number, student_id, department, borrowing_limit) VALUES (?, ?, ?, ?, ?, ?, ?)";
        } else if (user instanceof Professor) {
            sql = "INSERT INTO professor_users (id, name, email, phone_number, department, borrowing_limit) VALUES (?, ?, ?, ?, ?, ?)";
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }
    
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            UUID userId = user.getId() != null ? user.getId() : UUID.randomUUID();
            stmt.setObject(1, userId);
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            if (user instanceof Student) {
                Student student = (Student) user;
                stmt.setString(5, student.getStudentId());
                stmt.setString(6, student.getDepartment());
                stmt.setInt(7, student.getBorrowingLimit());
            } else if (user instanceof Professor) {
                Professor professor = (Professor) user;
                stmt.setString(5, professor.getDepartment());
                stmt.setInt(6, professor.getBorrowingLimit());
            }
            stmt.executeUpdate();
        }
    }

    private void updateLibraryUsers(Connection conn, User user) throws SQLException {
        String sql = "UPDATE library_users SET email = ?, phone_number = ? WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPhoneNumber());
            stmt.setString(3, user.getName());
            stmt.executeUpdate();
        }
    }

    private void updateSpecificUserTable(Connection conn, User user) throws SQLException {
        String sql;
        if (user instanceof Student) {
            sql = "UPDATE student_users SET email = ?, phone_number = ?, student_id = ?, department = ? WHERE name = ?";
        } else if (user instanceof Professor) {
            sql = "UPDATE professor_users SET email = ?, phone_number = ?, department = ? WHERE name = ?";
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPhoneNumber());
            if (user instanceof Student) {
                Student student = (Student) user;
                stmt.setString(3, student.getStudentId());
                stmt.setString(4, student.getDepartment());
                stmt.setString(5, student.getName());
            } else if (user instanceof Professor) {
                Professor professor = (Professor) user;
                stmt.setString(3, professor.getDepartment());
                stmt.setString(4, professor.getName());
            }
            stmt.executeUpdate();
        }
    }

    private void deleteUserFromTables(Connection conn, String name) throws SQLException {
        String[] tables = {"library_users", "student_users", "professor_users"};
        for (String table : tables) {
            String sql = "DELETE FROM " + table + " WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.executeUpdate();
            }
        }
    }

    private User createUserFromResultSet(ResultSet rs, String table) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        String department = rs.getString("department");

        if ("student_users".equals(table)) {
            String studentId = rs.getString("student_id");
            return new Student(id, name, email, phoneNumber, studentId, department);
        } else {
            return new Professor(id, name, email, phoneNumber, department);
        }
    }

    private void handleException(String message, SQLException e) {
        LOGGER.severe(message + ": " + e.getMessage());
        e.printStackTrace();
    }
}