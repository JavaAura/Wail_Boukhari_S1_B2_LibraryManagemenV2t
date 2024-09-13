package com.library.model;

import java.time.LocalDate;
import java.util.UUID;

public class Loan {
    private UUID id;

    private String documentTitle;
    private String userName;
    private LocalDate loanDate;
    private LocalDate returnDate;

    public Loan(UUID id, String documentTitle, String userName, LocalDate loanDate, LocalDate returnDate) {
        this.id = id;

        this.documentTitle = documentTitle;
        this.userName = userName;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
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