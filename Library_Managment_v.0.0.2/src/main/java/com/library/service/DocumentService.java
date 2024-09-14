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
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class DocumentService {
    private final DocumentDAO documentDAO;
    private final Map<String, Document> documentCache;

    public DocumentService() {
        this.documentDAO = DocumentDAOImpl.getInstance();
        this.documentCache = new HashMap<>();
        loadDocumentsIntoCache();
    }

    private void loadDocumentsIntoCache() {
        List<Document> documents = documentDAO.getAllDocuments();
        for (Document document : documents) {
            documentCache.put(document.getTitle(), document);
        }
    }

    public Optional<Document> getDocumentByTitle(String title) {
        return Optional.ofNullable(documentCache.get(title));
    }

    public Optional<Document> getDocumentById(UUID documentId) {
        return documentDAO.getDocumentById(documentId);
    }

    public void addDocument(Document document) {
        if (document.getId() == null) {
            document.setId(UUID.randomUUID());
        }
        documentDAO.addDocument(document);
        documentCache.put(document.getTitle(), document);
    }

    public void updateDocument(Document document) {
        documentDAO.updateDocument(document);
        documentCache.put(document.getTitle(), document);
    }

    public void deleteDocument(UUID documentId) {
        Optional<Document> document = documentDAO.getDocumentById(documentId);
        if (document.isPresent()) {
            documentCache.remove(document.get().getTitle());
        }
        documentDAO.deleteDocument(documentId);
    }

    public List<Document> getAllDocuments() {
        return new ArrayList<>(documentCache.values());
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
