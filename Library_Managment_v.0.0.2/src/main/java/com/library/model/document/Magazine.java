package com.library.model.document;

import java.util.UUID;

public class Magazine extends Document {
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
}