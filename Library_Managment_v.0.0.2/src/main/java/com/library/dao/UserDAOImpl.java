package com.library.dao;

import com.library.model.user.User;
import com.library.model.user.Student;
import com.library.model.user.Professor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDAOImpl implements UserDAO {
    private static UserDAOImpl instance;
    private final Connection connection;

    private UserDAOImpl() {
        try {
            this.connection = PostgresConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    public static UserDAOImpl getInstance() {
        if (instance == null) {
            instance = new UserDAOImpl();
        }
        return instance;
    }

    @Override
    public void addUser(User user) {
        String sql = "INSERT INTO " + getTableName(user) + " (id, name, email, phone_number, borrowing_limit, type, ";
        sql += getSpecificColumns(user) + ") VALUES (?, ?, ?, ?, ?, ?, " + getSpecificValues(user) + ")";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, user.getId());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setInt(5, user.getBorrowingLimit());
            stmt.setString(6, user.getType());
            setSpecificParameters(stmt, user, 7);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding user", e);
        }
    }

    private String getTableName(User user) {
        if (user instanceof Student) return "student_users";
        if (user instanceof Professor) return "professor_users";
        throw new IllegalArgumentException("Unknown user type");
    }

    private String getSpecificColumns(User user) {
        if (user instanceof Student) return "student_id, department";
        if (user instanceof Professor) return "department";
        throw new IllegalArgumentException("Unknown user type");
    }

    private String getSpecificValues(User user) {
        if (user instanceof Student) return "?, ?";
        if (user instanceof Professor) return "?";
        throw new IllegalArgumentException("Unknown user type");
    }

    private void setSpecificParameters(PreparedStatement stmt, User user, int startIndex) throws SQLException {
        if (user instanceof Student) {
            stmt.setString(startIndex, ((Student) user).getStudentId());
            stmt.setString(startIndex + 1, ((Student) user).getDepartment());
        } else if (user instanceof Professor) {
            stmt.setString(startIndex, ((Professor) user).getDepartment());
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }
    }

    @Override
    public void updateUser(User user) {
        String sql = "UPDATE library_users SET name = ?, email = ?, phone_number = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPhoneNumber());
            stmt.setObject(4, user.getId());
            stmt.executeUpdate();

            if (user instanceof Student) {
                updateStudentDetails((Student) user);
            } else if (user instanceof Professor) {
                updateProfessorDetails((Professor) user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user", e);
        }
    }

    private void updateStudentDetails(Student student) throws SQLException {
        String sql = "UPDATE student_users SET student_id = ?, department = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, student.getStudentId());
            stmt.setString(2, student.getDepartment());
            stmt.setObject(3, student.getId());
            stmt.executeUpdate();
        }
    }

    private void updateProfessorDetails(Professor professor) throws SQLException {
        String sql = "UPDATE professor_users SET department = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, professor.getDepartment());
            stmt.setObject(2, professor.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deleteUser(UUID userId) {
        String sql = "DELETE FROM library_users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user", e);
        }
    }

    @Override
    public Optional<User> getUserById(UUID userId) {
        String sql = "SELECT * FROM library_users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByName(String name) {
        String sql = "SELECT * FROM library_users WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user by name", e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM library_users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all users", e);
        }
        return users;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phoneNumber = rs.getString("phone_number");
        int borrowingLimit = rs.getInt("borrowing_limit");

        if (borrowingLimit == 3) {
            return mapToStudent(id, name, email, phoneNumber);
        } else if (borrowingLimit == 5) {
            return mapToProfessor(id, name, email, phoneNumber);
        } else {
            throw new IllegalArgumentException("Unknown user type");
        }
    }

    private Student mapToStudent(UUID id, String name, String email, String phoneNumber) throws SQLException {
        String sql = "SELECT student_id, department FROM student_users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String studentId = rs.getString("student_id");
                String department = rs.getString("department");
                return new Student(id, name, email, phoneNumber, studentId, department);
            }
        }
        throw new SQLException("Student details not found for user ID: " + id);
    }

    private Professor mapToProfessor(UUID id, String name, String email, String phoneNumber) throws SQLException {
        String sql = "SELECT department FROM professor_users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String department = rs.getString("department");
                return new Professor(id, name, email, phoneNumber, department);
            }
        }
        throw new SQLException("Professor details not found for user ID: " + id);
    }
}