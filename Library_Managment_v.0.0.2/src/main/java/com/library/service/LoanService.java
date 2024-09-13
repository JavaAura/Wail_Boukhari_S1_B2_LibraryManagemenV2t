package com.library.service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.library.dao.DocumentDAO;
import com.library.dao.LoanDAO;
import com.library.dao.UserDAO;
import com.library.model.Loan;
import com.library.model.user.User;

public class LoanService {
    private final LoanDAO loanDAO;
    private final UserDAO userDAO;
    private final DocumentDAO documentDAO;

    public LoanService() {
        this.loanDAO = LoanDAO.getInstance();
        this.userDAO = UserDAO.getInstance();
        this.documentDAO = DocumentDAO.getInstance();
    }

    public void loanDocument(String documentTitle, String userName) {
        User user = userDAO.findByName(userName)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist: " + userName));
    
        if (!documentDAO.documentExists(documentTitle)) {
            throw new IllegalArgumentException("Document does not exist: " + documentTitle);
        }
    
        if (isDocumentLoaned(documentTitle)) {
            throw new IllegalArgumentException("Document is already loaned: " + documentTitle);
        }
    
        int currentLoans = getCurrentLoansCount(userName);
        if (currentLoans >= user.getBorrowingLimit()) {
            throw new IllegalArgumentException("User has reached their maximum borrowing limit of " + user.getBorrowingLimit() + " documents.");
        }
    
        Loan loan = new Loan(null, documentTitle, userName, LocalDate.now(), null);
        try {
            loanDAO.save(loan);
        } catch (Exception e) {
            throw new RuntimeException("Unable to process loan. Please try again later.");
        }
    }

    private int getCurrentLoansCount(String userName) {
        return (int) loanDAO.findAll().stream()
                .filter(loan -> loan.getUserName().equals(userName) && loan.getReturnDate() == null)
                .count();
    }

    public void returnDocument(String documentTitle, String userName) {
        Optional<Loan> loanOpt = loanDAO.findByDocumentAndUser(documentTitle, userName);
        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            loan.setReturnDate(LocalDate.now());
            try {
                loanDAO.update(loan);
            } catch (Exception e) {
                throw new RuntimeException("Unable to process return. Please try again later.");
            }
        } else {
            throw new IllegalArgumentException("No active loan found for this document and user.");
        }
    }

    public List<Loan> getAllLoans() {
        try {
            return loanDAO.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve loans. Please try again later.");
        }
    }

    public boolean isDocumentLoaned(String documentTitle) {
        try {
            return loanDAO.findAll().stream()
                    .anyMatch(loan -> loan.getDocumentTitle().equals(documentTitle) && loan.getReturnDate() == null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to check document loan status. Please try again later.");
        }
    }
}