package com.library.model.user;

import java.util.UUID;

public class Student extends User {
    private String studentId;
    private String department;

    public Student(UUID id, String name, String email, String phoneNumber, String studentId, String department) {
        super(id, name, email, phoneNumber);
        this.studentId = studentId;
        this.department = department;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getType() {
        return "Student";
    }

    @Override
    public int getBorrowingLimit() {
        return 3; // Students can borrow up to 3 documents
    }
}