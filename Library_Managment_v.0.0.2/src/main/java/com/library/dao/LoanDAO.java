package com.library.dao;

import com.library.model.Loan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanDAO {
    void addLoan(Loan loan);
    void updateLoan(Loan loan);
    void deleteLoan(UUID loanId);
    Optional<Loan> getLoanById(UUID loanId);
    List<Loan> getAllLoans();
}
