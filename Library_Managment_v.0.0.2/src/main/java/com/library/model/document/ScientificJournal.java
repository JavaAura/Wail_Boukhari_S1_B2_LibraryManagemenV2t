package com.library.model.document;

import java.util.UUID;

public class ScientificJournal extends Document {
    private String researchField;

    public ScientificJournal(UUID id, String title, String author, String publisher, int publicationYear, String researchField) {
        super(id, title, author, publisher, publicationYear);
        this.researchField = researchField;
    }

    @Override
    public String getType() {
        return "ScientificJournal";
    }

    public String getResearchField() { return researchField; }
    public void setResearchField(String researchField) { this.researchField = researchField; }
}