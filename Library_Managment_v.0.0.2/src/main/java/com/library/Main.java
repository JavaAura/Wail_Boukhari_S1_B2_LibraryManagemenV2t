package com.library;

import com.library.app.ConsoleUI;
import com.library.service.DocumentService;
import com.library.service.LoanService;
import com.library.service.ReservationService;
import com.library.service.UserService;

public class Main {
    public static void main(String[] args) {
        DocumentService documentService = new DocumentService();
        UserService userService = new UserService();
        LoanService loanService = new LoanService();
        ReservationService reservationService = new ReservationService(loanService);

        // Initialize and start the console UI
        ConsoleUI consoleUI = new ConsoleUI(documentService, loanService, reservationService, userService);
        consoleUI.start();
    }
}