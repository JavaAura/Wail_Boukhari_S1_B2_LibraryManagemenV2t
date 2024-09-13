package com.library.model.document;

import java.util.UUID;

public class Book extends Document {
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
}