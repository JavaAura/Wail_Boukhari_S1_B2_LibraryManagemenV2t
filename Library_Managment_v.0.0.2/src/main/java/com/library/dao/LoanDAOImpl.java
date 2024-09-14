package com.library.dao;

import com.library.model.Loan;
import com.library.model.Reservation;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LoanDAOImpl implements LoanDAO {
    private static LoanDAOImpl instance;
    private final Connection connection;

    private LoanDAOImpl() {
        try {
            this.connection = PostgresConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    public static LoanDAOImpl getInstance() {
        if (instance == null) {
            instance = new LoanDAOImpl();
        }
        return instance;
    }

    @Override
    public void addLoan(Loan loan) {
        String sql = "INSERT INTO loans (id, document_id, user_id, loan_date, return_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, loan.getId());
            stmt.setObject(2, loan.getDocumentId());
            stmt.setObject(3, loan.getUserId());
            stmt.setDate(4, java.sql.Date.valueOf(loan.getLoanDate()));
            stmt.setDate(5, loan.getReturnDate() != null ? java.sql.Date.valueOf(loan.getReturnDate()) : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding loan", e);
        }
    }

    @Override
    public void updateLoan(Loan loan) {
        String sql = "UPDATE loans SET return_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, loan.getReturnDate() != null ? java.sql.Date.valueOf(loan.getReturnDate()) : null);
            stmt.setObject(2, loan.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Updating loan failed, no rows affected.");
            }
            
            // If the loan is being returned (return_date is not null), check for reservations
            if (loan.getReturnDate() != null) {
                checkAndProcessReservations(loan.getDocumentId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating loan: " + e.getMessage(), e);
        }
    }
private void checkAndProcessReservations(UUID documentId) {
    ReservationDAO reservationDAO = ReservationDAOImpl.getInstance();
    List<Reservation> reservations = reservationDAO.getReservationsForDocument(documentId);
    
    if (!reservations.isEmpty()) {
        Reservation oldestReservation = reservations.get(0);
        
        // Create a new loan for the oldest reservation
        Loan newLoan = new Loan(UUID.randomUUID(), documentId, oldestReservation.getUserId(), LocalDate.now(), null);
        addLoan(newLoan);
        
        // Remove the processed reservation
        reservationDAO.deleteReservation(documentId, oldestReservation.getUserId());
    }
}
    @Override
    public void deleteLoan(UUID loanId) {
        String sql = "DELETE FROM loans WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, loanId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting loan", e);
        }
    }

    @Override
    public Optional<Loan> getLoanById(UUID loanId) {
        String sql = "SELECT * FROM loans WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, loanId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching loan by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all loans", e);
        }
        return loans;
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        UUID documentId = (UUID) rs.getObject("document_id");
        UUID userId = (UUID) rs.getObject("user_id");
        LocalDate loanDate = rs.getDate("loan_date").toLocalDate();
        LocalDate returnDate = rs.getDate("return_date") != null ? rs.getDate("return_date").toLocalDate() : null;
        return new Loan(id, documentId, userId, loanDate, returnDate);
    }

}