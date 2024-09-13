package com.library.model;

import java.time.LocalDate;
import java.util.UUID;

public class Reservation {
    private UUID id;
    private String documentTitle;
    private String userName;
    private LocalDate reservationDate;

    public Reservation(UUID id, String documentTitle, String userName, LocalDate reservationDate) {
        this.id = id;
        this.documentTitle = documentTitle;
        this.userName = userName;
        this.reservationDate = reservationDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }
}