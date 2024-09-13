package com.library.model.document;

import com.library.model.interfaces.Borrowable;
import com.library.model.interfaces.Reservable;
import java.util.UUID;

public class Magazine extends Document implements Borrowable, Reservable {
    private int issueNumber;

    public Magazine(UUID id, String title, String author, String publisher, int publicationYear, int issueNumber) {
        super(id, title, author, publisher, publicationYear);
        this.issueNumber = issueNumber;
    }

    public int getIssueNumber() {
        return issueNumber;
    }

    public void setIssueNumber(int issueNumber) {
        this.issueNumber = issueNumber;
    }

    @Override
    public String getType() {
        return "Magazine";
    }

    @Override
    public void borrow() {
        // Implement borrow logic
    }

    @Override
    public void returnDocument() {
        // Implement return logic
    }

    @Override
    public void reserve() {
        // Implement reserve logic
    }

    @Override
    public void cancelReservation() {
        // Implement cancel reservation logic
    }
}