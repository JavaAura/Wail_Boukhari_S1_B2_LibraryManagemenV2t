package com.library.dao;

import com.library.model.Reservation;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReservationDAOImpl implements ReservationDAO {
    private static ReservationDAOImpl instance;
    private final Connection connection;

    private ReservationDAOImpl() {
        try {
            this.connection = PostgresConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to establish database connection", e);
        }
    }

    public static ReservationDAOImpl getInstance() {
        if (instance == null) {
            instance = new ReservationDAOImpl();
        }
        return instance;
    }

    @Override
    public void addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (id, document_id, user_id, reservation_date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, reservation.getId());
            stmt.setObject(2, reservation.getDocumentId());
            stmt.setObject(3, reservation.getUserId());
            stmt.setDate(4, java.sql.Date.valueOf(reservation.getReservationDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding reservation", e);
        }
    }
    @Override
    public List<Reservation> getReservationsForDocument(UUID documentId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.* FROM reservations r " +
                "JOIN all_documents d ON r.document_id = d.id " +
                "WHERE d.id = ? " +
                "ORDER BY r.reservation_date ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, documentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting reservations for document", e);
        }
        return reservations;
    }

    @Override
    public void updateReservation(Reservation reservation) {
        String sql = "UPDATE reservations SET reservation_date = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(reservation.getReservationDate()));
            stmt.setObject(2, reservation.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating reservation", e);
        }
    }

    @Override
    public void deleteReservation(UUID documentId, UUID userId) {
        String sql = "DELETE FROM reservations WHERE document_id = ? AND user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, documentId);
            stmt.setObject(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reservation", e);
        }
    }

    @Override
    public Optional<Reservation> getReservationById(UUID reservationId) {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setObject(1, reservationId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting reservation by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all reservations", e);
        }
        return reservations;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        UUID documentId = (UUID) rs.getObject("document_id");
        UUID userId = (UUID) rs.getObject("user_id");
        LocalDate reservationDate = rs.getDate("reservation_date").toLocalDate();
        return new Reservation(id, documentId, userId, reservationDate);
    }

}