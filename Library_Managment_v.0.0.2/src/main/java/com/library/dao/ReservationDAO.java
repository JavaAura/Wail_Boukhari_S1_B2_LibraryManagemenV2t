package com.library.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.library.model.Reservation;

public class ReservationDAO {
    private static ReservationDAO instance;

    private ReservationDAO() {
        // private constructor to prevent instantiation
    }

    public static ReservationDAO getInstance() {
        if (instance == null) {
            instance = new ReservationDAO();
        }
        return instance;
    }

    public void save(Reservation reservation) {
        String sql = "INSERT INTO reservations (id, document_title, user_name, reservation_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setReservationParameters(stmt, reservation);
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleException("Error saving reservation", e);
        }
    }

    public List<Reservation> findAll() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                reservations.add(createReservationFromResultSet(rs));
            }
        } catch (SQLException e) {
            handleException("Error finding all reservations", e);
        }
        return reservations;
    }

    public void delete(String documentTitle, String userName) {
        String sql = "DELETE FROM reservations WHERE document_title = ? AND user_name = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documentTitle);
            stmt.setString(2, userName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleException("Error deleting reservation", e);
        }
    }

    public Optional<Reservation> findByDocumentAndUser(String documentTitle, String userName) {
        String sql = "SELECT * FROM reservations WHERE document_title = ? AND user_name = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documentTitle);
            stmt.setString(2, userName);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(createReservationFromResultSet(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            handleException("Error finding reservation by document and user", e);
            return Optional.empty();
        }
    }

    public List<Reservation> findByDocument(String documentTitle) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE document_title = ? ORDER BY reservation_date ASC";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, documentTitle);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(createReservationFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            handleException("Error finding reservations by document", e);
        }
        return reservations;
    }

    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        return new Reservation(
            (UUID) rs.getObject("id"),
            rs.getString("document_title"),
            rs.getString("user_name"),
            rs.getDate("reservation_date").toLocalDate()
        );
    }

    private void setReservationParameters(PreparedStatement stmt, Reservation reservation) throws SQLException {
        stmt.setObject(1, reservation.getId() != null ? reservation.getId() : UUID.randomUUID());
        stmt.setString(2, reservation.getDocumentTitle());
        stmt.setString(3, reservation.getUserName());
        stmt.setDate(4, java.sql.Date.valueOf(reservation.getReservationDate()));
    }

    private void handleException(String message, SQLException e) {
        // Log the exception or handle it according to your application's needs
        System.err.println(message + ": " + e.getMessage());
    }
}