package com.library.service;

import com.library.dao.DocumentDAO;
import com.library.dao.DocumentDAOImpl;
import com.library.model.document.Document;
import com.library.model.document.Book;
import com.library.model.document.Magazine;
import com.library.model.document.ScientificJournal;
import com.library.model.document.UniversityThesis;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DocumentService {
    private final DocumentDAO documentDAO;

    public DocumentService() {
        this.documentDAO = DocumentDAOImpl.getInstance();
    }

    public void addDocument(Document document) {
        if (document.getId() == null) {
            document.setId(UUID.randomUUID());
        }
        documentDAO.addDocument(document);
    }

    public void updateDocument(Document document) {
        documentDAO.updateDocument(document);
    }

    public void deleteDocument(UUID documentId) {
        documentDAO.deleteDocument(documentId);
    }

    public Optional<Document> getDocumentById(UUID documentId) {
        return documentDAO.getDocumentById(documentId);
    }

    public Optional<Document> getDocumentByTitle(String title) {
        return documentDAO.getDocumentByTitle(title);
    }

    public List<Document> getAllDocuments() {
        return documentDAO.getAllDocuments();
    }

    public List<Document> searchDocuments(String searchTerm) {
        return documentDAO.searchDocuments(searchTerm);
    }

    public Document createBook(String title, String author, String publisher, int publicationYear, String isbn) {
        return new Book(null, title, author, publisher, publicationYear, isbn);
    }

    public Document createMagazine(String title, String author, String publisher, int publicationYear, int issueNumber) {
        return new Magazine(null, title, author, publisher, publicationYear, issueNumber);
    }

    public Document createScientificJournal(String title, String author, String publisher, int publicationYear, String researchField) {
        return new ScientificJournal(null, title, author, publisher, publicationYear, researchField);
    }

    public Document createUniversityThesis(String title, String author, String publisher, int publicationYear, String university, String field) {
        return new UniversityThesis(null, title, author, publisher, publicationYear, university, field);
    }
}
