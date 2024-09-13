package com.library.service;

import com.library.dao.ReservationDAO;
import com.library.dao.ReservationDAOImpl;
import com.library.dao.UserDAO;
import com.library.dao.UserDAOImpl;
import com.library.dao.DocumentDAO;
import com.library.dao.DocumentDAOImpl;
import com.library.model.Reservation;
import com.library.model.document.Document;
import com.library.model.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final UserDAO userDAO;
    private final DocumentDAO documentDAO;
    private final LoanService loanService;

    public ReservationService(LoanService loanService) {
        this.reservationDAO = ReservationDAOImpl.getInstance();
        this.userDAO = UserDAOImpl.getInstance();
        this.documentDAO = DocumentDAOImpl.getInstance();
        this.loanService = loanService;
    }

    public void reserveDocument(String documentTitle, String userName) {
        Optional<Document> document = documentDAO.getDocumentByTitle(documentTitle);
        Optional<User> user = userDAO.getUserByName(userName);

        if (document.isEmpty() || user.isEmpty()) {
            throw new IllegalArgumentException("Document or user not found");
        }

        // Check if the document is already borrowed
        try {
            loanService.borrowDocument(documentTitle, userName);
            // If successful, the document is available, so we don't need to reserve it
            throw new IllegalStateException("Document is available for borrowing, no need to reserve");
        } catch (IllegalStateException e) {
            // Document is already borrowed, proceed with reservation
            Reservation reservation = new Reservation(UUID.randomUUID(), document.get().getId(), user.get().getId(), LocalDate.now());
            reservationDAO.addReservation(reservation);
        }
    }

    public void cancelReservation(String documentTitle, String userName) {
        Optional<Document> document = documentDAO.getDocumentByTitle(documentTitle);
        Optional<User> user = userDAO.getUserByName(userName);

        if (document.isEmpty() || user.isEmpty()) {
            throw new IllegalArgumentException("Document or user not found");
        }

        reservationDAO.deleteReservation(document.get().getId(), user.get().getId());
    }

    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }
}
