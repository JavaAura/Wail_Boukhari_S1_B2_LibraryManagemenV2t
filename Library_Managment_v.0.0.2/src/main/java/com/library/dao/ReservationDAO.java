package com.library.dao;

import com.library.model.Reservation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReservationDAO {
    void addReservation(Reservation reservation);
    void updateReservation(Reservation reservation);
    Optional<Reservation> getReservationById(UUID reservationId);
    List<Reservation> getAllReservations();
    void deleteReservation(UUID documentId, UUID userId);
    
}
