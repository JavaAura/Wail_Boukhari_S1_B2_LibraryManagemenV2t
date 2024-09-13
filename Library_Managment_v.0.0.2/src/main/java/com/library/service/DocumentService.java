package com.library.service;

import com.library.dao.DocumentDAO;
import com.library.model.document.Document;
import java.util.List;
import java.util.Optional;

public class DocumentService {
    private final DocumentDAO documentDAO;

    public DocumentService() {
        this.documentDAO = DocumentDAO.getInstance();
    }

    public void addDocument(Document document) {
        documentDAO.save(document);
    }

    public void updateDocument(Document document) {
        documentDAO.update(document);
    }

    public void deleteDocument(String title) {
        documentDAO.delete(title);
    }

    public List<Document> getAllDocuments() {
        return documentDAO.findAll();
    }

    public Optional<Document> getDocumentByTitle(String title) {
        return documentDAO.findByTitle(title);
    }

    public List<Document> searchDocuments(String searchTerm) {
        return documentDAO.findAll().stream()
                .filter(doc -> doc.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        doc.getAuthor().toLowerCase().contains(searchTerm.toLowerCase()))
                .toList();
    }
}