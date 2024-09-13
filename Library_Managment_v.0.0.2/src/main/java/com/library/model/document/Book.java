package com.library.model.document;

import com.library.model.interfaces.Borrowable;
import com.library.model.interfaces.Reservable;
import java.util.UUID;

public class Book extends Document implements Borrowable, Reservable {
    private String isbn;

    public Book(UUID id, String title, String author, String publisher, int publicationYear, String isbn) {
        super(id, title, author, publisher, publicationYear);
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    @Override
    public String getType() {
        return "Book";
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