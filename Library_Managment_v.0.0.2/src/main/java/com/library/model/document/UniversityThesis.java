package com.library.model.document;

import java.util.UUID;

public class UniversityThesis extends Document {
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
}