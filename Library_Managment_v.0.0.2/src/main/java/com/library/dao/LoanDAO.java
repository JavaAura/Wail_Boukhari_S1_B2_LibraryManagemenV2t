package com.library.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.library.model.Loan;

public class LoanDAO {
    private static LoanDAO instance;

    private LoanDAO() {}

    public static LoanDAO getInstance() {
        if (instance == null) {
            instance = new LoanDAO();
        }
        return instance;
    }

    public void save(Loan loan) {
        String sql = "INSERT INTO loans (id, document_title, user_name, loan_date, return_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setLoanParameters(stmt, loan);
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleException("Error saving loan", e);
        }
    }

    public void update(Loan loan) {
        String sql = "UPDATE loans SET return_date = ? WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, loan.getReturnDate() != null ? java.sql.Date.valueOf(loan.getReturnDate()) : null);
            stmt.setObject(2, loan.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleException("Error updating loan", e);
        }
    }

    public List<Loan> findAll() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                loans.add(createLoanFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error finding all loans", e);
        }
        return loans;
    }

    public Optional<Loan> findByDocumentAndUser(String documentTitle, String userName) {
        String sql = "SELECT * FROM loans WHERE document_title = ? AND user_name = ? AND return_date IS NULL";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documentTitle);
            stmt.setString(2, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(createLoanFromResultSet(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            handleException("Error finding loan by document and user", e);
            return Optional.empty();
        }
    }

    private Loan createLoanFromResultSet(ResultSet rs) throws SQLException {
        return new Loan(
            (UUID) rs.getObject("id"),
            rs.getString("document_title"),
            rs.getString("user_name"),
            rs.getDate("loan_date").toLocalDate(),
            Optional.ofNullable(rs.getDate("return_date")).map(Date::toLocalDate).orElse(null)
        );
    }

    private void setLoanParameters(PreparedStatement stmt, Loan loan) throws SQLException {
        stmt.setObject(1, loan.getId() != null ? loan.getId() : UUID.randomUUID());
        stmt.setString(2, loan.getDocumentTitle());
        stmt.setString(3, loan.getUserName());
        stmt.setDate(4, java.sql.Date.valueOf(loan.getLoanDate()));
        stmt.setDate(5, loan.getReturnDate() != null ? java.sql.Date.valueOf(loan.getReturnDate()) : null);
    }

    private void handleException(String message, SQLException e) {
        // Log the exception or handle it according to your application's needs
        System.err.println(message + ": " + e.getMessage());
    }
}