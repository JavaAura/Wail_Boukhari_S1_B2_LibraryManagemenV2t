package com.library.model.document;

import com.library.model.interfaces.Borrowable;
import com.library.model.interfaces.Reservable;
import java.util.UUID;

public class UniversityThesis extends Document implements Borrowable, Reservable {
    private String university;
    private String field;

    public UniversityThesis(UUID id, String title, String author, String publisher, int publicationYear, String university, String field) {
        super(id, title, author, publisher, publicationYear);
        this.university = university;
        this.field = field;
    }

    @Override
    public String getType() {
        return "UniversityThesis";
    }

    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }

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