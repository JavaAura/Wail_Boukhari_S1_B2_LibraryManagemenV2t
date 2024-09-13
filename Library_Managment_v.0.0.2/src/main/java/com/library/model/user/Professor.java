package com.library.model.user;

import java.util.UUID;

public class Professor extends User {
    private String department;

    public Professor(UUID id, String name, String email, String phoneNumber, String department) {
        super(id, name, email, phoneNumber);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getType() {
        return "Professor";
    }

    @Override
    public int getBorrowingLimit() {
        return 5; // Professors can borrow up to 5 documents
    }
}