package com.library.service;

import com.library.dao.LoanDAO;
import com.library.dao.LoanDAOImpl;
import com.library.dao.UserDAO;
import com.library.dao.UserDAOImpl;
import com.library.dao.DocumentDAO;
import com.library.dao.DocumentDAOImpl;
import com.library.model.Loan;
import com.library.model.document.Document;
import com.library.model.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LoanService {
    private final LoanDAO loanDAO;
    private final UserDAO userDAO;
    private final DocumentDAO documentDAO;

    public LoanService() {
        this.loanDAO = LoanDAOImpl.getInstance();
        this.userDAO = UserDAOImpl.getInstance();
        this.documentDAO = DocumentDAOImpl.getInstance();
    }

    public void borrowDocument(String documentTitle, String userName) {
        Optional<Document> document = documentDAO.getDocumentByTitle(documentTitle);
        Optional<User> user = userDAO.getUserByName(userName);

        if (document.isEmpty() || user.isEmpty()) {
            throw new IllegalArgumentException("Document or user not found");
        }

        int activeLoans = getActiveLoansCount(user.get().getId());
        if (activeLoans >= user.get().getBorrowingLimit()) {
            throw new IllegalStateException("User has reached their borrowing limit");
        }

        Loan loan = new Loan(UUID.randomUUID(), document.get().getId(), user.get().getId(), LocalDate.now(), null);
        loanDAO.addLoan(loan);
    }

    public void returnDocument(String documentTitle, String userName) {
        Optional<Document> document = documentDAO.getDocumentByTitle(documentTitle);
        Optional<User> user = userDAO.getUserByName(userName);

        if (document.isEmpty() || user.isEmpty()) {
            throw new IllegalArgumentException("Document or user not found");
        }

        Optional<Loan> activeLoan = getActiveLoan(document.get().getId(), user.get().getId());
        if (activeLoan.isEmpty()) {
            throw new IllegalStateException("No active loan found for this document and user");
        }

        Loan loan = activeLoan.get();
        loan.setReturnDate(LocalDate.now());
        loanDAO.updateLoan(loan);
    }

    public List<Loan> getAllLoans() {
        return loanDAO.getAllLoans();
    }

    private int getActiveLoansCount(UUID userId) {
        return (int) loanDAO.getAllLoans().stream()
                .filter(loan -> loan.getUserId().equals(userId) && loan.getReturnDate() == null)
                .count();
    }

    private Optional<Loan> getActiveLoan(UUID documentId, UUID userId) {
        return loanDAO.getAllLoans().stream()
                .filter(loan -> loan.getDocumentId().equals(documentId) &&
                        loan.getUserId().equals(userId) &&
                        loan.getReturnDate() == null)
                .findFirst();
    }
}
