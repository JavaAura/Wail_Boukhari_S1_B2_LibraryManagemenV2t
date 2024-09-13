package com.library.model;

import java.time.LocalDate;
import java.util.UUID;

public class Loan {
    private UUID id;
    private UUID documentId;
    private UUID userId;
    private LocalDate loanDate;
    private LocalDate returnDate;

    public Loan(UUID id, UUID documentId, UUID userId, LocalDate loanDate, LocalDate returnDate) {
        this.id = id;
        this.documentId = documentId;
        this.userId = userId;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}