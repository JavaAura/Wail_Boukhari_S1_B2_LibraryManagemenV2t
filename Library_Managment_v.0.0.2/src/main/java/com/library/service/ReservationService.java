package com.library.service;

import com.library.dao.ReservationDAO;
import com.library.model.Reservation;
import java.time.LocalDate;
import java.util.List;

public class ReservationService {
	private final ReservationDAO reservationDAO;
	private final LoanService loanService;

	public ReservationService(LoanService loanService) {
		this.reservationDAO = ReservationDAO.getInstance();
		this.loanService = loanService;
	}

	public void reserveDocument(String documentTitle, String userName) {
		if (!loanService.isDocumentLoaned(documentTitle)) {
			throw new IllegalArgumentException("Document is not currently loaned and doesn't need reservation.");
		}
		Reservation reservation = new Reservation(null, documentTitle, userName, LocalDate.now());
		try {
			reservationDAO.save(reservation);
		} catch (Exception e) {
			throw new RuntimeException("Unable to process reservation. Please try again later.");
		}
	}

	public void cancelReservation(String documentTitle, String userName) {
		try {
			reservationDAO.delete(documentTitle, userName);
		} catch (Exception e) {
			throw new RuntimeException("Unable to cancel reservation. Please try again later.");
		}
	}

	public List<Reservation> getAllReservations() {
		try {
			return reservationDAO.findAll();
		} catch (Exception e) {
			throw new RuntimeException("Unable to retrieve reservations. Please try again later.");
		}
	}

	public List<Reservation> getReservationsForDocument(String documentTitle) {
		try {
			return reservationDAO.findByDocument(documentTitle);
		} catch (Exception e) {
			throw new RuntimeException("Unable to retrieve reservations for the document. Please try again later.");
		}
	}
}